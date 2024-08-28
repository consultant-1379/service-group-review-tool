import { Component, OnInit} from '@angular/core';
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {NotificationLog} from "@eds/vanilla";
import {Title} from "@angular/platform-browser";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-reference-deployments-view',
  templateUrl: './reference-deployments-view.component.html',
  styleUrls: ['./reference-deployments-view.component.css']
})
export class ReferenceDeploymentsViewComponent implements OnInit {

  public deploymentTypes;
  public referenceDeployments;
  public deploymentLoadDrivers;

  public sizes;

  constructor(private referenceDeploymentsService: ReferenceDeploymentsService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT - Reference Deployments");
  }

  ngOnInit(): void {
    this.getDeploymentTypes()
    this.getReferenceDeployments();
    this.getDeploymentLoadDrivers();
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        this.deploymentTypes = data;
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve reference deployments from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("ReferenceDeployments loaded.")
    );
  }

  getReferenceDeployments() {
    this.referenceDeploymentsService.getReferenceDeployments().subscribe(
      data => {
        this.referenceDeployments = data;
        this.sizes = new Set(this.referenceDeployments.map(ref => ref.deploymentType.sizeKey));
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve reference deployments from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("ReferenceDeployments loaded.")
    );
  }

  getDeploymentLoadDrivers() {
    this.referenceDeploymentsService.getDeploymentLoadDrivers().subscribe(
      data => {
        this.deploymentLoadDrivers = data;
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve deployment load drivers from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => {
        console.log("Deployment Dependant Load Drivers loaded.");

      }
    );

  }

}
