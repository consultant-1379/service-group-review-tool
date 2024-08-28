import { Component, OnInit } from '@angular/core';
import {GenericLoadDriversService} from "../../services/genericLoadDriver/generic-load-drivers.service";
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";

@Component({
  selector: 'app-generic-load-drivers-admin',
  templateUrl: './generic-load-drivers-admin.component.html',
  styleUrls: ['./generic-load-drivers-admin.component.css']
})
export class GenericLoadDriversAdminComponent implements OnInit {

  public genericLoadDrivers;
  public selectedGenericLoadDriver;

  public newGenericLoadDriver;

  constructor(private genericLoadDriverService: GenericLoadDriversService,
              private titleService: Title) {
    this.titleService.setTitle("RMRT Admin - Generic Load Drivers");

    this.newGenericLoadDriver = {
      name: 'New Generic Load Driver',
      description: 'Description',
      value: 0
    };
    this.selectedGenericLoadDriver = this.newGenericLoadDriver;
  }

  ngOnInit(): void {
    this.getGenericLoadDrivers();
  }

  getGenericLoadDrivers() {
    this.genericLoadDriverService.getGenericLoadDrivers().subscribe(
      data => {
        this.genericLoadDrivers = data
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve generic load drivers from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        });
        console.log(err);
      },
      () => console.log("Generic Load Drivers loaded.")
    );
  }

  submit() {
    if (this.selectedGenericLoadDriver == this.newGenericLoadDriver) {
      this.genericLoadDriverService.createGenericLoadDriver(this.selectedGenericLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Created new Generic Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Error in POST for new Generic Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedGenericLoadDriver);
          this.getGenericLoadDrivers();
        }
      );
    } else {
      this.genericLoadDriverService.updateGenericLoadDriver(this.selectedGenericLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Updated Generic Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Error in PUT for new Generic Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedGenericLoadDriver);
          this.getGenericLoadDrivers();
        }
      );
    }
  }

  delete() {
    let confirmed = confirm(`Are you sure you want to delete ${this.selectedGenericLoadDriver.name}?`);
    if (confirmed) {
      this.genericLoadDriverService.deleteGenericLoadDriver(this.selectedGenericLoadDriver).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Deleted Generic Load Driver', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedGenericLoadDriver.name, // String
            description: 'Error deleting, please contact admin', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          this.getGenericLoadDrivers();
        }
      )
    }
  }
}
