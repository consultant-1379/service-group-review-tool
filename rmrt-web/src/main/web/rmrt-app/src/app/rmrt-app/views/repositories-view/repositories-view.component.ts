import {Component, OnInit} from '@angular/core';
import { RepositoriesService } from "../../services/repositories/repositories.service";
import {NotificationLog} from "@eds/vanilla";
import {GenericLoadDriversService} from "../../services/genericLoadDriver/generic-load-drivers.service";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-repositories-view',
  templateUrl: './repositories-view.component.html',
  styleUrls: ['./repositories-view.component.css']
})
export class RepositoriesViewComponent implements OnInit {
  public repositories;

  constructor(private repositoriesService: RepositoriesService,
              private titleService:Title) {
    this.titleService.setTitle("RMRT - Repositories")
  }

  ngOnInit(): void {
    this.getRepositories();
  }

  getRepositories() {
    this.repositoriesService.getRepositories().subscribe(
      data => {
        this.repositories = data
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve repositories from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        })
      },
      () => console.log("Repositories loaded.")
    );
  }
}
