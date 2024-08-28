import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";
import {FileSystemsService} from "../../services/fileSystems/file-systems.service";
import {NotificationLog} from "@eds/vanilla";
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-file-systems-view',
  templateUrl: './file-systems-view.component.html',
  styleUrls: ['./file-systems-view.component.css']
})
export class FileSystemsViewComponent implements OnInit {

  public fileSystems;
  public deploymentTypes;

  constructor(private fileSystemService: FileSystemsService,
              private referenceDeploymentsService: ReferenceDeploymentsService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT - File Systems");
  }

  ngOnInit(): void {
    this.getFileSystems();
    this.getDeploymentTypes();
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
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

  getFileSystems() {
    this.fileSystemService.getFileSystems().subscribe(
      data => {
        this.fileSystems = data
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve file system information from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("File Systems loaded.")
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
