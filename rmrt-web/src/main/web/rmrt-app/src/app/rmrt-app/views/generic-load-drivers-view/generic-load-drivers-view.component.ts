import { Component, OnInit } from '@angular/core';
import {NotificationLog} from "@eds/vanilla";
import {GenericLoadDriversService} from "../../services/genericLoadDriver/generic-load-drivers.service";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-generic-load-drivers-view',
  templateUrl: './generic-load-drivers-view.component.html',
  styleUrls: ['./generic-load-drivers-view.component.css']
})
export class GenericLoadDriversViewComponent implements OnInit {
  public genericLoadDrivers;

  constructor(private genericLoadDriverService: GenericLoadDriversService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT - Generic Load Drivers");
  }

  ngOnInit(): void {
    this.getGenericLoadDrivers()
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
        })
      },
      () => console.log("Repositories loaded.")
    );
  }
}
