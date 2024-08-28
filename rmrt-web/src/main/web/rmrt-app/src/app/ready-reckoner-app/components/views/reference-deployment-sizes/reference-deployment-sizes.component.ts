import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { formatPercentage } from "../../../shared/percentFormatter";
import { Accordion } from "@eds/vanilla";
import { Router } from "@angular/router";
import { AppComponentStateService } from "../../../services/appState/app-state.service";
import { FormActionsService } from "../../../services/form/formActions/form-actions.service";
import { Subscription } from "rxjs";

@Component({
  selector: 'app-reference-deployment-sizes',
  templateUrl: './reference-deployment-sizes.component.html',
  styleUrls: ['./reference-deployment-sizes.component.css']
})
export class ReferenceDeploymentSizesComponent implements OnInit, AfterViewInit, OnDestroy {
  public deploymentSizes;
  public supportedDeployments;
  public unsupportedDeployments;
  public totalNumberOfElements;
  public dataToExport = {};
  public exportDataEvent$: Subscription;

  constructor(private router: Router,
              private appStateService: AppComponentStateService,
              private formActionsService: FormActionsService) {

    const navigation = this.router.getCurrentNavigation();

    // data to display
    this.deploymentSizes = navigation.extras.state['result'];

    // for exporting
    this.dataToExport['inputs'] = navigation.extras.state['inputsForResult'];
    this.dataToExport['result'] = navigation.extras.state['result'];
  }

  ngOnInit(): void {

    setTimeout( () => {
      this.appStateService.changeState('results');
    }, 0);


    this.initialiseData();

    this.subscribeExportDataEvent();
  }



  subscribeExportDataEvent() {
    this.exportDataEvent$ = this.formActionsService.exportData$.subscribe( (flag) => {
      if(flag == true) {
        this.exportData();
      }
    });
  }


  ngAfterViewInit() {
    this.initAccordion();
  }


  ngOnDestroy() {
    this.exportDataEvent$.unsubscribe();
  }


  initialiseData() {
    this.supportedDeployments = this.deploymentSizes["supported"];
    this.unsupportedDeployments = this.deploymentSizes["notSupported"];
    this.totalNumberOfElements = this.deploymentSizes["totalNumberOfElements"];

    this.supportedDeployments.forEach( (deployment) => {
      formatPercentage(deployment.keyDimensioningValues);
      formatPercentage(deployment.mediationComponents);
    });

    this.unsupportedDeployments.forEach( (deployment) => {
      formatPercentage(deployment.keyDimensioningValues);
      formatPercentage(deployment.mediationComponents);
    });
  }


  initAccordion() {
    const accordions = document.querySelectorAll('.accordion');

    if (accordions) {
      Array.from(accordions).forEach((accordionDOM: HTMLElement) => {
        const accordion = new Accordion(accordionDOM);
        accordion.init();
      });
    }
  }


  goToInputs() {
    this.router.navigate(['readyReckoner']);
  }


  exportData() {
    let dataStr = JSON.stringify(this.dataToExport, null, 4);
    let dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);

    let exportFileDefaultName = 'reference-deployment-sizes.json';

    let linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileDefaultName);
    linkElement.click();

    this.formActionsService.toggleExportData(false);
  }

}
