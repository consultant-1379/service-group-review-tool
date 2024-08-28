import { Component, OnInit } from '@angular/core';
import {FileSystemsService} from "../../services/fileSystems/file-systems.service";
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-file-systems-admin',
  templateUrl: './file-systems-admin.component.html',
  styleUrls: ['./file-systems-admin.component.css']
})
export class FileSystemsAdminComponent implements OnInit {


  public fileSystems;
  public newFileSystem;
  public selectedFileSystem;

  public physicalNames;
  public cloudNames;

  constructor(private fileSystemService: FileSystemsService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT Admin - File Systems");

    this.newFileSystem = {
      physicalMapping: 'Physical',
      cloudMapping: 'Cloud',
      customMappings: [],
      capacities: [],
    };
    this.selectedFileSystem = this.newFileSystem;
  }

  ngOnInit(): void {
    this.getFileSystems();
    this.getNames();
  }

  getNames() {
    this.fileSystemService.getFileSystemNames().subscribe(
      data => {
        this.cloudNames = data['cloud'];
        this.physicalNames = data['physical'];
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve file system information from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("File System Names loaded.")
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

  submit() {
    let name = this.selectedFileSystem.physicalMapping + ' // ' + this.selectedFileSystem.cloudMapping;
    this.selectedFileSystem.customMappings = Array.from(new Set(this.selectedFileSystem.customMappings));

    if (this.selectedFileSystem == this.newFileSystem) {
      this.fileSystemService.createFileSystem(this.selectedFileSystem).subscribe(
        data => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Created new File System Mapping', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Error in POST for new File System Mapping', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedFileSystem);
          this.getFileSystems();
        }
      );
    } else {
      this.fileSystemService.updateFileSystem(this.selectedFileSystem).subscribe(
        data => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Updated File System Mapping', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Error in PUT for new File System Mapping', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedFileSystem);
          this.getFileSystems();
        }
      );
    }
  }

  delete() {
    let name = this.selectedFileSystem.physicalMapping + ' // ' + this.selectedFileSystem.cloudMapping;
    let confirmed = confirm(`Are you sure you want to delete ${name}?`);
    if (confirmed) {
      this.fileSystemService.deleteFileSystem(this.selectedFileSystem).subscribe(
        data => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Deleted File System Mapping' , // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: name, // String
            description: 'Error deleting, please contact admin', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          this.getFileSystems();
        }
      )
    }
  }

  trackByIndex(index: number, obj: any): any {
    return index;
  }

  addNewMapping() {
    let x = this.selectedFileSystem.customMappings.length;
    this.selectedFileSystem.customMappings[x] = "New Mapping";
  }

  removeMapping(i: number) {
    this.selectedFileSystem.customMappings = this.selectedFileSystem.customMappings.filter((value, index, arr) => {
      return index != i;
    });
  }
}
