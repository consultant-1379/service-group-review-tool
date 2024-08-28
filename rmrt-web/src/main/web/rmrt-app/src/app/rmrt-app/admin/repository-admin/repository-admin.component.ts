import { Component, OnInit } from '@angular/core';
import {RepositoriesService} from "../../services/repositories/repositories.service";
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {ReferenceDeploymentsService} from "../../services/referenceDeployments/reference-deployments.service";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-repository-admin',
  templateUrl: './repository-admin.component.html',
  styleUrls: ['./repository-admin.component.css']
})
export class RepositoryAdminComponent implements OnInit {

  public selectedRepository;

  public repositories;
  public newRepository;

  public deploymentTypes;

  constructor(private referenceDeploymentsService: ReferenceDeploymentsService,
              private repositoriesService: RepositoriesService,
              private deploymentTypesService: DeploymentTypesService,
              private titleService: Title) {
    this.titleService.setTitle("RMRT - Repositories");

    this.newRepository = {
      name: "New Repository",
      project: "Project",
      filePath: "File Path",
      instanceLimits: {}
    }
    this.selectedRepository = this.newRepository;
  }

  ngOnInit(): void {
    this.getRepositories();
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

  submit() {
    if(this.selectedRepository == this.newRepository) {
      this.repositoriesService.createRepository(this.selectedRepository).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Created new Repository', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Error creating new Repository', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedRepository);
          this.getRepositories();
        }
      );
    }
    else {
      this.repositoriesService.updateRepository(this.selectedRepository).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Updated Repository', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Error updating repository', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(this.selectedRepository);
          this.getRepositories();
        }
      );
    }
  }

  delete() {
    let confirmed = confirm(`Are you sure you want to delete ${this.selectedRepository.name}?`);
    if (confirmed) {
      this.repositoriesService.deleteRepository(this.selectedRepository).subscribe(
        data => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Deleted Repository', // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: this.selectedRepository.name, // String
            description: 'Error deleting, please contact admin', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          this.getRepositories();
        }
      )
    }
  }

}
