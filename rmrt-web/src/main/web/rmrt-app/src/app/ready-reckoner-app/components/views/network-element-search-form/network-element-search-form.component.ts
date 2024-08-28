import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { FormBuilder, FormArray, Validators, FormGroup } from "@angular/forms";
import { validateDuplicates } from "../../../shared/duplicateValueValidator";
import { JsonReaderService } from "../../../services/jsonReader/json-reader.service";
import { NetworkElementsService } from "../../../services/networkElements/network-elements.service";
import { AppComponentStateService } from "../../../services/appState/app-state.service";
import { NavigationExtras, Router } from '@angular/router';
import { Observable, Subscription } from "rxjs";
import { NotificationLog } from "@eds/vanilla";
import { FormActionsService } from "../../../services/form/formActions/form-actions.service";
import { FormDataService } from "../../../services/form/formData/form-data.service";
import { FormControlsService } from "../../../services/form/formControls/form-controls.service";
import { networkElementTypeValidator } from "../../../shared/networkElementTypeValidator";
import { validateNetworkElementInputs } from "../../../shared/networkElementInputsValidator";
import { FormStateService } from "../../../services/form/formState/form-state.service";
import { PreviousRouteService } from "../../../services/previousRoute/previous-route.service";

@Component({
  selector: 'app-network-element-search-form',
  templateUrl: './network-element-search-form.component.html',
  styleUrls: ['./network-element-search-form.component.css']
})

export class NetworkElementSearchFormComponent implements OnInit, OnDestroy {
  public networkElementSearchForm: FormGroup;
  public importJsonRowInputs;
  public networkElements;
  public latestRelease;
  public resultsPending;
  public calculatedRefDeployments;
  public inputForExport;
  public hardCodedDataForAPI;
  public responseData;
  public supportedDeploymentsCondition;
  private formSubmitSubscription$: Subscription;
  private importSubscription$: Subscription;


  constructor(private fb: FormBuilder,
              private jsonReader: JsonReaderService,
              private neService: NetworkElementsService,
              private appStateService: AppComponentStateService,
              private router: Router,
              private formActionsService: FormActionsService,
              private formDataService: FormDataService,
              private formControlsService: FormControlsService,
              private formStateService: FormStateService,
              private previousRouteService: PreviousRouteService)
  {

  }


  ngOnInit() {

    this.getNetworkElements();

    setTimeout( () => {
      this.appStateService.changeState('form');
    }, 0);

    this.createForm();

    this.subscribeFormActionEvents();

    this.subscribeDeletedRow();

  }


  //stop subscriptions when component is destroyed
  ngOnDestroy() {
    this.formSubmitSubscription$.unsubscribe();
    this.importSubscription$.unsubscribe();
  }


  //whenever form actions events occur, the next value received from subscription will cause an action to be performed
  subscribeFormActionEvents() {
    this.formSubmitSubscription$ = this.formActionsService.submitForm$.subscribe( (flag) => {
      if(flag == true && this.networkElementSearchRows.length > 0) {

        if(document.getElementById('submit-ne') != null) {
          document.getElementById('submit-ne').click();
        }

      }
    });


    this.importSubscription$ = this.formActionsService.importFormData$.subscribe( (flag) => {
      if(flag == true) {
        this.importFormData();
      }
    });
  }


  //whenever a row within the form will be deleted, there will be a check for other rows with same output load
  //driver. If any rows with same output are found, there will be a new API request to get the output value of
  //those rows.
  subscribeDeletedRow() {

    this.formActionsService.deletedRowOutput$.subscribe( (outputKey) => {
      if(outputKey && this.networkElementSearchRows.length > 0) {
        //get the remaining form rows that share the same output key
        const remainingRowsWithOutputKey = this.formControlsService.getFormRowsByOutput(this.networkElementSearchRows, outputKey);

        if(remainingRowsWithOutputKey.length > 0) {
          const updatedRequest = this.formDataService.updateOutputRequest(remainingRowsWithOutputKey, outputKey);

          this.neService.evaluateOutput(updatedRequest).subscribe( (data) => {
            this.formControlsService.updateFormOutputValues(remainingRowsWithOutputKey, data);
          });
        }
      }

    });
  }



  getNetworkElements() {

    this.neService.networkElementTypes.subscribe( (data) => {
      this.networkElements = data["networkElementTypes"];
      this.latestRelease = data["latestRelease"];

      // console.log(this.networkElements);
    },

() => {
      NotificationLog.setNotification({
        title: 'Error retrieving resources', // String
        description: 'Error getting data from server. Please contact system administrator if problem persists', // String
        timestamp: new Date() // Date
      });
    });
  }


  //if previous route is results page, then use the cached form, otherwise create empty form
  createForm() {
    if(this.previousRouteService.getPreviousUrl() == '/readyReckoner/results') {

      this.formStateService.form$.subscribe( (form) => {
        if(form != null) {
          this.networkElementSearchForm = form;
          const networkElementSearchRows = this.networkElementSearchForm.get('networkElementSearchRows') as FormArray;

          // Iterate through each FormGroup in the FormArray
          networkElementSearchRows.controls.forEach((formGroup: FormGroup) => {
          const networkElementTypeControl = formGroup.get('networkElementType');
          if (networkElementTypeControl) {
            networkElementTypeControl.disable();
          }
        });
        }
      });

    } else {
      this.createEmptyForm();
    }
  }


  createEmptyForm() {
    //create form, the form rows will be stored in an array for dynamic adding / removing
    this.networkElementSearchForm = this.fb.group({
      networkElementSearchRows: this.fb.array([], { validators: [Validators.required, validateDuplicates()] })
    } );
  }



  get networkElementSearchRows() {
    try {
      return this.networkElementSearchForm.get('networkElementSearchRows') as FormArray;
    } catch (e) {
      console.log(e);
    }

  }


  //creates and returns a formGroup representing the added form row. Takes as optional parameter form row data.
  addInputRowFormGroup(inputRowData?) {
    //when data coming from json data will be defined
    if(inputRowData != undefined) {

      /*
        if the import has outdated input/output load drivers for network element then do not
        create input/output controls, only create networkElementType control so that the user
        can see which network element has outdated data, user can then delete row and re-select
      */
      if(
          (this.formDataService.checkUpToDateInputs(this.networkElements, inputRowData) == false)
              ||
          (this.formDataService.checkUpToDateOutput(this.networkElements, inputRowData) == false)
              ||
          (this.formDataService.checkUpToDatePrePopulatedInput(this.networkElements, inputRowData) == false)
        )

      {

        return this.fb.group({
          networkElementType: [inputRowData["networkElementType"], {validators: [networkElementTypeValidator(this.networkElements, true) ], updateOn: "submit"} ]
        }, {validators: validateNetworkElementInputs, updateOn: "submit"});

      }


      //if import data is valid create controls as normal
      let rowFg = this.fb.group({
        networkElementType: [inputRowData["networkElementType"], {validators: [networkElementTypeValidator(this.networkElements, true) ], updateOn: "submit"} ],
        inputs: this.fb.group([] , {validators: Validators.required, updateOn: "submit"})
      });

      this.formControlsService.createInputControls(rowFg, inputRowData);

      //create output and inputPrePopulated if exist for network element
      this.formControlsService.createOutputControls(rowFg, inputRowData);

      this.formControlsService.createInputPrePopulatedControls(rowFg, inputRowData);

      return rowFg;

      //in the case where the user adds a input row from the form
    } else {
      return this.fb.group({
        networkElementType: ['', {validators: [networkElementTypeValidator(this.networkElements, false) ], updateOn: "submit"} ],
        inputs: this.fb.group([] , {validators: Validators.required})
      });
    }
  }


  addNetworkElementSearchRow(inputRowData?) {

    //prevent row from being validated until submission
    this.formActionsService.toggleFormSubmit(false);

    //add a new row of input to the form rows array
    this.networkElementSearchRows.push(this.addInputRowFormGroup(inputRowData));
  }


  onSubmit() {

    if(this.networkElementSearchForm.valid) {

      //gather the form data -> will be used when user chooses to export the data for later use
      this.inputForExport = this.formDataService.gatherFormData(this.networkElementSearchForm, this.networkElements);

      //save the form state, in case user goes back to form to make changes
      this.saveFormState();

      //create the request object to submit
      const submitObject = this.formDataService.getSubmissionContents(this.networkElementSearchRows);

      //make request to the API
      this.getRefDeployments(submitObject);

    }

  }



  saveFormState() {
    this.formStateService.saveFormState(this.networkElementSearchForm);
  }


  //submit form contents to the API and navigate to results page
  getRefDeployments(networkElementInputs) {
    console.log('submitting');
    console.log(networkElementInputs);
    this.resultsPending = true;
    this.hardCodedDataForAPI = {"numberGNodeBBasebandNe":10,"numberBasebandRadioNRCells":100}
    this.neService.calculateReferenceDeployments(this.hardCodedDataForAPI)
    .subscribe((hardCodedRes) =>{
      this.responseData = hardCodedRes;
      this.supportedDeploymentsCondition = this.responseData.supported.find(deploymentTypes=>{return deploymentTypes.name === "Extra Small ENM on Openstack Cloud"});
      if(this.supportedDeploymentsCondition){
        this.neService.calculateReferenceDeployments(networkElementInputs)
        .subscribe((data) => {
          this.calculatedRefDeployments = data;
        },
        () => {
          this.resultsPending = false;
          this.appStateService.changeState('form');

          NotificationLog.setNotification({
            title: 'Error getting results', // String
            description: 'Error calculating the reference deployment sizes. Please contact system administrator if problem persist', // String
            timestamp: new Date() // Date
          });
        },
        () => {
          this.appStateService.changeState('results');

          //data for export to be used in the next navigation route
          const navigationExtras: NavigationExtras = {
            state: {
              result: this.calculatedRefDeployments,
              inputsForResult: this.inputForExport
            }
          }

          this.router.navigate(['/readyReckoner/results'] , navigationExtras);
        });
      } else {
        this.resultsPending = false;
          this.appStateService.changeState('form');

          NotificationLog.setNotification({
            title: 'Error getting results', // String
            description: 'If the ENM Ready Reckoner is unavailable or any other queries related to the tool can be raised in the Q&A tool via the “Raise A Question?” banner under Product=ENM,  Question Type = Product Line, Technical Area = Hardware / Deployment.', // String
            timestamp: new Date() // Date
          });
      }

    },() => {
      this.resultsPending = false;
      this.appStateService.changeState('form');

      NotificationLog.setNotification({
        title: 'Error getting results', // String
        description: 'If the ENM Ready Reckoner is unavailable or any other queries related to the tool can be raised in the Q&A tool via the “Raise A Question?” banner under Product=ENM,  Question Type = Product Line, Technical Area = Hardware / Deployment.', // String
        timestamp: new Date() // Date
      });
    });
  }


  //warn user before navigating away or reloading page about unsaved changes
  @HostListener('window:beforeunload')
  canDeactivate(): boolean | Promise<boolean> | Observable<boolean> {

    // Allow synchronous navigation (`true`) if form is unchanged
    if ( (this.networkElementSearchRows.length == 0) || (this.calculatedRefDeployments != null)) {
      return true;
    }

    return confirm("The changes you made on this page may be lost if you navigate away.\n" +
      "Are you sure you want to navigate away?");
  }



  importFormData() {
    try {
      document.getElementById('file-import-input').click();
    } catch (err) {
      //error may occur when user submits form while page still loading, this error is ignored
    }
  }


  //method called when user imports the form from an external JSON file
  async handleFileInput(files: FileList) {
    let jsonFile: File = files.item(0);

    //clear the form if a file is imported
    while (this.networkElementSearchRows.length !== 0) {
      this.networkElementSearchRows.removeAt(0);
    }

    this.importJsonRowInputs = await this.jsonReader.getJsonContent(jsonFile).then( (data) => this.jsonReader.parseJson(data));

    if(this.importJsonRowInputs == null) {
      NotificationLog.setNotification({
        title: 'Error parsing data', // String
        description: 'Error parsing the data. Please ensure the data follows the export json format.', // String
        timestamp: new Date() // Date
      });
      return;
    }


    for(let i = 0; i < this.importJsonRowInputs.length; ++i) {
      this.addNetworkElementSearchRow(this.importJsonRowInputs[i]);
    }
  }

}
