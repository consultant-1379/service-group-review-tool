import { Component, OnInit } from '@angular/core';
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-reference-deployments-admin',
  templateUrl: './reference-deployments-admin.component.html',
  styleUrls: ['./reference-deployments-admin.component.css']
})
export class ReferenceDeploymentsAdminComponent implements OnInit {

  public selectedReferenceDeployment;
  public newReferenceDeployment;

  public referenceDeployments;
  public deploymentLoadDrivers;
  public deploymentTypes;

  public copy;
  public newName;

  constructor(private referenceDeploymentsService: ReferenceDeploymentsService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT Admin - Reference Deployments");

    this.newReferenceDeployment = {
      name: "New Reference Deployment",
      description: "Description",
      deploymentType: {}
    }
    this.selectedReferenceDeployment = this.newReferenceDeployment;
    this.copy = false;
  }

  ngOnInit(): void {
    this.getReferenceDeployments();
    this.getDeploymentLoadDrivers();
    this.getDeploymentTypes();
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getAllDeploymentTypes().subscribe(
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
        console.log("ReferenceDeployments loaded.");
        this.newReferenceDeployment.deploymentType = this.deploymentTypes[0];
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
      () => {
        console.log("ReferenceDeployments loaded.")
      }
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
        console.log("Deployment Dependant Load Drivers loaded.");
      }
    );
  }

  compareDeploymentTypes(a,b) {
    if(a === null || a === undefined) {
      a = {sizeKey: "unknown"}
    }
    if(b === null || b === undefined) {
      b = {sizeKey: "unknown"}
    }
    return a.sizeKey === b.sizeKey;
  }

  submit() {
    if (this.selectedReferenceDeployment == this.newReferenceDeployment) {
      this.referenceDeploymentsService.createReferenceDeployment(this.selectedReferenceDeployment, this.deploymentLoadDrivers).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
            description: 'Created new Reference Deployment', // String
            timestamp: new Date() // Date
          });
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
            description: 'Error in POST for new Reference Deployment', // String
            timestamp: new Date() // Date
          });
        },
        () => {
          console.log(this.selectedReferenceDeployment);
          this.getReferenceDeployments();
          this.getDeploymentLoadDrivers();
        }
      );
    } else {
      this.referenceDeploymentsService.updateReferenceDeployment(this.selectedReferenceDeployment, this.deploymentLoadDrivers, this.copy, this.newName).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
            description: 'Updated Reference Deployment', // String
            timestamp: new Date() // Date
          });
          //TODO add deployment lds

        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
            description: 'Error in PUT for new Reference Deployment', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedReferenceDeployment);
          this.getReferenceDeployments();
          this.getDeploymentLoadDrivers();
        }
      );
    }
  }

  delete() {
    let confirmed = confirm(`Are you sure you want to delete ${this.selectedReferenceDeployment.name}?`);
    if (confirmed) {
      this.referenceDeploymentsService.deleteReferenceDeployment(this.selectedReferenceDeployment).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
            description: 'Deleted Reference Deployment', // String
            timestamp: new Date() // Date
          });
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedReferenceDeployment.name, // String
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

  deploymentChanged() {
    this.copy = false;
    this.newName = this.selectedReferenceDeployment.name;
  }

}
