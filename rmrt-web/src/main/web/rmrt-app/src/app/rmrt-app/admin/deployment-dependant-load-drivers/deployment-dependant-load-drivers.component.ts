import { Component, OnInit } from '@angular/core';
import {RepositoriesService} from "../../services/repositories/repositories.service";
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-deployment-dependant-load-drivers',
  templateUrl: './deployment-dependant-load-drivers.component.html',
  styleUrls: ['./deployment-dependant-load-drivers.component.css']
})
export class DeploymentDependantLoadDriversComponent implements OnInit {

  public selectedDeploymentLoadDriver;
  public newDeploymentLoadDriver;

  public referenceDeployments;
  public deploymentLoadDrivers;

  public deploymentTypes;
  public copy;

  constructor(private referenceDeploymentsService: ReferenceDeploymentsService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT Admin - Deployment Load Driver");

    this.newDeploymentLoadDriver = {
      name: "New Deployment Load Driver",
      description: "Description",
      values: {},
    }
    this.selectedDeploymentLoadDriver = this.newDeploymentLoadDriver;
  }

  ngOnInit(): void {
    this.getReferenceDeployments();
    this.getDeploymentLoadDrivers();
    this.getDeploymentTypes();
  }

  getDeploymentTypes() {

    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        this.deploymentTypes = data;

      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve deployment types from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => {
        console.log("ReferenceDeployments loaded.")
      });
  }

  getReferenceDeployments() {
    this.referenceDeploymentsService.getReferenceDeployments().subscribe(
      data => {
        this.referenceDeployments = data;
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
        this.deploymentLoadDrivers = this.deploymentLoadDrivers.filter(x => x.name != "enm_deployment_type");
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve deployment load drivers from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => {
        console.log("Deployment Dependant Load Drivers loaded.");}
    );
  }

  submit() {
    if (this.selectedDeploymentLoadDriver == this.newDeploymentLoadDriver) {
      this.referenceDeploymentsService.createDeploymentLoadDriver(this.selectedDeploymentLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Created new Deployment Load Driver', // String
            timestamp: new Date() // Date
          });
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Error in POST for new Deployment Load Driver', // String
            timestamp: new Date() // Date
          });
        },
        () => {
          console.log(this.selectedDeploymentLoadDriver);
          this.getReferenceDeployments();
          this.getDeploymentLoadDrivers();
        }
      );
    } else {
      this.referenceDeploymentsService.updateDeploymentLoadDriver(this.selectedDeploymentLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Updated Deployment Load Driver', // String
            timestamp: new Date() // Date
          });
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Error in PUT for new Deployment Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedDeploymentLoadDriver);
          this.getReferenceDeployments();
          this.getDeploymentLoadDrivers();
        }
      );
    }
  }

  delete() {
    let confirmed = confirm(`Are you sure you want to delete ${this.selectedDeploymentLoadDriver.name}?`);
    if (confirmed) {
      this.referenceDeploymentsService.deleteDeploymentLoadDriver(this.selectedDeploymentLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Deleted Deployment Load Driver', // String
            timestamp: new Date() // Date
          });
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentLoadDriver.name, // String
            description: 'Error deleting, please contact admin', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          this.getReferenceDeployments();
          this.getDeploymentLoadDrivers();
        }
      )
    }
  }
}
