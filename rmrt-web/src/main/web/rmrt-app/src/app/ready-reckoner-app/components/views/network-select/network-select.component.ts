/*
* Component initially displaying select component. When user chooses network element, the inputs/ ouput / inputPrePopulated are created.
* Represents a row of input
*
* Requests for getting output control value are created when the user fills in the form.
*
* Requests for inputPrePopulated control are created when user selects a NE with inputPrePopulated or when
* user fills in the value for inputPrePopulated. Rows with same inputPrePopulated load driver are searched for
* and changed to a new value when user fills in from form.
*
* Component using ChangeDetectionStrategy.OnPush to significantly increase performance of the form.
* This can be noticed when many rows of input are added into the form without degrading performance.
*
* ChangeDetectorRef used for showing validation errors when form is submitted.
*
* */


import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { Select } from "@eds/vanilla";
import { findAndRemoveDuplicateError } from "../../../shared/duplicateValueValidator";
import { NetworkElementsService } from "../../../services/networkElements/network-elements.service";
import { Subject } from "rxjs";
import { debounceTime, distinctUntilChanged, switchMap } from "rxjs/operators";
import { FormControlsService } from "../../../services/form/formControls/form-controls.service";
import { FormActionsService } from "../../../services/form/formActions/form-actions.service";
import { AbstractControl, FormGroup } from "@angular/forms";
import { FormDataService } from "../../../services/form/formData/form-data.service";

@Component({
  selector: 'app-network-select',
  templateUrl: './network-select.component.html',
  styleUrls: ['./network-select.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NetworkSelectComponent implements AfterViewInit {
  @Input() networkElements: [];
  @Input() parentForm;
  @Input() controlIndex;
  @Input() formSearchRows;
  @Input() selectedNetworkElementString;

  public selectedNetworkElement;
  public requestOutputBody = new Subject();
  public requestPreInputValue = new Subject();
  public formSubmitAttempt;
  public selectId;

  constructor(private formControlService: FormControlsService,
              private neService: NetworkElementsService,
              private formActionsService: FormActionsService,
              private formDataService: FormDataService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    //generate a unique number to use for select id in the dom
    this.selectId = 'select-' + Math.floor(new Date().valueOf() * Math.random());

    //any future requests to quickEval will be piped through this method
    this.handleRequestsForQuickEval(this.requestOutputBody);
    this.handleRequestsForQuickEval(this.requestPreInputValue);

    this.checkDataImport();

    //subscribe to form submit attempt event for showing validation errors
    this.formActionsService.submitForm$.subscribe( (flag) => {
      if(flag == true) {
        this.formSubmitAttempt = flag;

        //if the form is submitted, trigger change detection to display errors, if any
        this.changeDetectorRef.markForCheck();
      }
    });

  }

  ngAfterViewInit() {
    this.initSelect();
  }


  /*Requests sent to quickEval are piped through this method.
  * Requests are sent to quickEval for network elements with either "inputPrePopulated" or
  * "output" load drivers.
  *
  *
  * inputPrePopulated requests are sent when the user selects a network element that
  * has inputPrePopulated load driver. The request will be sent to get the default value
  * of that load driver. When the response from the API is received, the form
  * will be scanned for other rows that share the same inputPrePopulated load driver.
  * The value of all those rows will be changed accordingly
  *
  * For output requests, if multiple rows in the form have the same output load driver then the request object
  * will be based on the input of all the rows that share the same output load driver. The API response
  * will be the output value for all rows with same output load driver.
  * */
  handleRequestsForQuickEval(requestBodyType) {
    //minimise the number of requests sent to the backend
    requestBodyType.pipe(

      //send request after 2 second
      debounceTime(2000),

      //only send a request if the request body's value has changed
      distinctUntilChanged(),

      switchMap( (requestBody) => this.neService.evaluateOutput(requestBody) )
    ).subscribe( (response) => {

      if(requestBodyType == this.requestOutputBody) {

        //change output value for all form rows with output key
        for(const formGroup of this.formControlService.getFormRowsByOutput(this.parentForm.get('networkElementSearchRows'), this.outputControlKey) ) {
          formGroup.get('outputs').setValue(response);
        }

      } else if (requestBodyType == this.requestPreInputValue) {
        try{
          //all rows that share the same inputPrePopulated load driver will have their value changed
          const formRowsByInputPrePopulated: AbstractControl[] = this.formControlService.getFormRowsByPreInput(this.formSearchRows, Object.keys(response)[0]);

          formRowsByInputPrePopulated.forEach( (row) => {
            row.get('inputPrePopulated').setValue(response);
          });

        } catch (e) {
          console.log(e);
        }

      }

    });
  }



  checkDataImport() {

    //when form data is imported, the network element type's value will be provided
    if(this.selectedNetworkElementString != "") {
      this.selectedNetworkElement = this.networkElements.find( (ne) => ne["networkElementType"] == this.selectedNetworkElementString);
      if(this.selectedNetworkElement) {
        //if selected network element has outputs then create an output request
        if(Object.keys(this.selectedNetworkElement['outputs']).length > 0) {
          this.createOutputRequestObject();
        }
      }
    }

  }

  //Initialise the Select component
  initSelect() {

    const selectDOM = <HTMLElement> document.getElementById(this.selectId);

    if (selectDOM) {
      const selectComponent = new Select(selectDOM);
      selectComponent.init();
    }

  }


  //when the user selects a network element from the form, create the inputs for the network element
  onSelect(network) {
    this.selectedNetworkElement = network;

    //update the value of the networkElementType control
    this.thisRowFormGroup.get('networkElementType')
      .setValue(this.selectedNetworkElement["networkElementType"]);

    this.formControlService.createInputControls(this.thisRowFormGroup, this.selectedNetworkElement);

    //some network elements do not have output or inputPrePopulated
    if( Object.keys(this.selectedNetworkElement['outputs']).length > 0 ) {
      this.formControlService.createOutputControls(this.thisRowFormGroup, this.selectedNetworkElement);
    }

    //if selected NE has inputPrePopulated, then create form controls for each inputPrePopulated of the row
    if( Object.keys(this.selectedNetworkElement['inputPrePopulated']).length > 0 ) {
      this.formControlService.createInputPrePopulatedControls(this.thisRowFormGroup, this.selectedNetworkElement);

      //*****
      let inputPrePopulatedLoadDriver = Object.keys(this.thisRowFormGroup.get('inputPrePopulated').value)[0];


      this.checkRowsInputPrePopulatedValue(inputPrePopulatedLoadDriver);
    }
    if(this.thisRowFormGroup.get('networkElementType').valid){
      this.thisRowFormGroup.get('networkElementType').disable();
    }
  }



  //when user selects network element with inputPrePopulated load driver then this method will
  //scan for other rows with the same inputPrePopulated load driver. If other rows are found then
  //check for their values, if a value is found then set the current row's value with the found
  //value, otherwise make a request to get the default value for the load driver.
  checkRowsInputPrePopulatedValue(inputPrePopulatedLoadDriver) {
    let otherFormRows = this.formSearchRows.controls.filter( (row) =>
      row != this.thisRowFormGroup
      &&
      row.get('inputPrePopulated') != null
      &&
      Object.keys(row.get('inputPrePopulated').value)[0] == inputPrePopulatedLoadDriver);

    //if there are other rows within the form that share the same inputPrePopulated load driver,
    //then do not make an API request if the value for the inputPrePopulated load driver is already set. (changed by the user or already calculated by backend)
    if(otherFormRows.length > 0) {

      for(let i = 0; i < otherFormRows.length; ++i) {
        let rowInputPrePopulatedValue = otherFormRows[i].get('inputPrePopulated').get(inputPrePopulatedLoadDriver).value;

        if(rowInputPrePopulatedValue) {
          this.thisRowFormGroup.get('inputPrePopulated').get(inputPrePopulatedLoadDriver).setValue(rowInputPrePopulatedValue);

          return;
        }
      }

    }

    //if this point reached, then make an API request to get the default value for inputPrePopulated load driver
    this.requestPreInputValue.next( this.formDataService.createOutputRequestForInputPre(this.thisRowFormGroup) );

  }




  //getter for the current form row
  get thisRowFormGroup() {
    return this.parentForm.get('networkElementSearchRows').at(this.controlIndex);
  }


  /*
    Get the output load driver of the row.
    After removing the row, update the output value for other rows with same output load driver
  */
  deleteRow() {

    const outputKey = this.outputControlKey;

    findAndRemoveDuplicateError(this.thisRowFormGroup);

    this.formSearchRows.removeAt(this.controlIndex);

    this.formActionsService.updateRowsByOutput(outputKey);

  }


  inputControlInvalid(inputFormControl) {
    if(this.thisRowFormGroup.get('networkElementType').valid){
      this.thisRowFormGroup.get('networkElementType').disable();
    }
    return ( this.networkElementInputs.get(`${inputFormControl}`).invalid
      &&
      this.formSubmitAttempt == true);

  }


  outputControlInvalid(outputFormControl) {

    return ( this.thisRowFormGroup.get('outputs').get(`${outputFormControl}`).invalid
      &&
      this.formSubmitAttempt == true );

  }

  //getter for form control of network element type
  get networkElementTypeControl() {
    return this.thisRowFormGroup.get('networkElementType');
  }


  //create a request body for api to evaluate the output fields' value
  createOutputRequestObject(inputKey?, inputValue?) {

    const requestBody = this.formDataService.createOutputRequestObject(this.formSearchRows, this.thisRowFormGroup, inputKey, inputValue);

    if(requestBody) {
      this.requestOutputBody.next(requestBody);
    }

  }



  //getter for network element output -> some network elements have no output
  get outputControlKey() {
    if( this.thisRowFormGroup.get('outputs') != null) {
      return Object.keys(this.thisRowFormGroup.get('outputs').value)[0];
    }
  }


  //network element inputs form group
  get networkElementInputs() {
    return this.thisRowFormGroup.get('inputs') as FormGroup;
  }


  //inputPrePopulated check if invalid
  preInputInvalid(preInputValue) {
    return this.thisRowFormGroup.get('inputPrePopulated').get(preInputValue).invalid
      &&
    this.formSubmitAttempt == true;
  }


  //when there is a inputPrePopulated form control that is shared among multiple rows,
  //a change in the default value of that inputPrePopulated form control will cause
  //a change in all form controls with same inputPrePopulated load driver
  changePreInputDefaultValue(preInputLoadDriver, value) {
    //get all the rows with the same inputPrePopulated load driver
    const rowsSameInputPre: AbstractControl[] = this.formControlService.getFormRowsByPreInput(this.formSearchRows, preInputLoadDriver);

    //change value for all form controls of the same inputPrePopulated load driver.
    rowsSameInputPre.forEach( (row) => {
      row.get('inputPrePopulated').get(preInputLoadDriver).setValue(Number(value));
    });
  }
}
