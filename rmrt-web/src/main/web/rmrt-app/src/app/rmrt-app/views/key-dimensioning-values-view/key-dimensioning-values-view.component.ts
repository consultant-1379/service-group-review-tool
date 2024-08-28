import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {KeyDimensioningValuesService} from "../../services/keyDimensioningValues/key-dimensioning-values.service";
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-key-dimensioning-values-view',
  templateUrl: './key-dimensioning-values-view.component.html',
  styleUrls: ['./key-dimensioning-values-view.component.css']
})
export class KeyDimensioningValuesViewComponent implements OnInit {

  public keyDimensioningValues;
  public deploymentTypes;

  constructor(private keyDimensioningValuesService: KeyDimensioningValuesService,
              private referenceDeploymentsService: ReferenceDeploymentsService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT - Key Dimensioning Values");
  }

  ngOnInit(): void {
    //TODO
    // Add key dims and fs home view
    // Add key Dim and FS display to Repository view
    // Add altered key dim and fs values to Change View

    this.getKeyDims();
    this.getDeploymentTypes()
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        console.log(data);
        this.deploymentTypes = data;

      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving deployment types', // String
          description: 'Could not access from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        });
        console.log(err);
      },
      () => {}
    );
  }

  getKeyDims() {
    this.keyDimensioningValuesService.getKeyDimensioningValues().subscribe(
      data => {
        this.keyDimensioningValues = data
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve KeyDimensioning Values information from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("Key Dimensioning Values loaded.")
    );
  }

  getTotal(fileSys: any, ref: any) {
    return this.formatValue(fileSys.totals[ref]);
  }

  getCapacity(fileSys: any, ref: any) {
    return this.formatValue(fileSys.capacities[ref]);
  }

  getPercent(fileSys: any, ref: any) {
    return this.formatValue(fileSys.percentages[ref]);
  }

  formatValue(value) {
    if (isNaN(value) || !isFinite(value) || value === 0) {
      return 0;
    } else {
      try {
        let x = Number(value);
        return Math.ceil(x).toString();
      } catch (e) {
        return 0;
      }
    }
  }

}
