import {Component, Input, OnInit } from '@angular/core';
import {Accordion, Dialog, NotificationLog} from "@eds/vanilla";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";
import {NewLoadDriverService} from "../../services/newLoadDrivers/new-load-driver.service";

@Component({
  selector: 'app-new-load-drivers-dialog',
  templateUrl: './new-load-drivers-dialog.component.html',
  styleUrls: ['./new-load-drivers-dialog.component.css']
})
export class NewLoadDriversDialogComponent implements OnInit {

  @Input() public newLoadDrivers;
  public deploymentTypes;

  constructor(private deploymentTypesService: DeploymentTypesService,
              private newLoadDriverService: NewLoadDriverService) {}

  ngOnInit(): void {
    this.getDeploymentTypes();
    const dialogDOM = document.getElementById('new-load-drivers-dialog');

    if (dialogDOM) {
      const dialog = new Dialog(dialogDOM);
      dialog.init();
    }

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
        console.log("Deployment Types loaded.")
      });
  }


  submit() {
    this.newLoadDriverService.createNewLoadDrivers(this.newLoadDrivers).subscribe(
      data => {
        NotificationLog.setNotification({
          title: "Success", // String
          description: 'Successfully created new Load Drivers', // String
          timestamp: new Date() // Date
        });
        setTimeout(() => location.reload(), 3000)
      },
      error => {
        NotificationLog.setNotification({
          title: "Error", // String
          description: 'Error in POST for new Load Drivers', // String
          timestamp: new Date() // Date
        });
        console.log(error);
      },
      () => {
        console.log(this.newLoadDrivers);
      }
    );
  }

}
