import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-deployment-types-admin',
  templateUrl: './deployment-types-admin.component.html',
  styleUrls: ['./deployment-types-admin.component.css']
})
export class DeploymentTypesAdminComponent implements OnInit {

  public deploymentTypes;

  public selectedDeploymentType;
  public newDeploymentType;

  constructor(private deploymentTypesService: DeploymentTypesService,
              private titleService: Title) {
    this.titleService.setTitle("RMRT Admin - Deployment Types");

    this.newDeploymentType = {
      sizeKey: "enm_deployment_type",
      displayName: "Display Name",
      visible: false,
      mappingsForFileSystems: null
    }
    this.selectedDeploymentType = this.newDeploymentType;
  }

  ngOnInit(): void {
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
        console.log("ReferenceDeployments loaded.")
      });
  }

  submit() {
    if(this.selectedDeploymentType == this.newDeploymentType) {
      this.deploymentTypesService.createDeploymentType(this.selectedDeploymentType).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Created new DeploymentType', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Error creating new DeploymentType', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedDeploymentType);
          this.getDeploymentTypes();
        }
      );
    }
    else {
      this.deploymentTypesService.updateDeploymentTypes(this.selectedDeploymentType).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Updated DeploymentType', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Error updating DeploymentType', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedDeploymentType);
          this.getDeploymentTypes();
        }
      );
    }
  }

  delete() {
    let confirmed = confirm(`Are you sure you want to delete ${this.selectedDeploymentType.sizeKey}?`);
    if (confirmed) {
      this.deploymentTypesService.deleteDeploymentTypes(this.selectedDeploymentType).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Deleted DeploymentType', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedDeploymentType.sizeKey, // String
            description: 'Error deleting, please contact admin', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          this.getDeploymentTypes();
        }
      )
    }
  }

}
