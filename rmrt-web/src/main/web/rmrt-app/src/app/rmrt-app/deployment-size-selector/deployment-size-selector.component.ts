import {Component, OnInit} from '@angular/core';
import {ReferenceDeploymentsService} from "../services/referenceDeployments/reference-deployments.service";
import {NotificationLog, Pill} from "@eds/vanilla";
import {DeploymentTypesService} from "../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-deployment-size-selector',
  templateUrl: './deployment-size-selector.component.html',
  styleUrls: ['./deployment-size-selector.component.css']
})
export class DeploymentSizeSelectorComponent implements OnInit {

  public deploymentTypes;

  constructor(private deploymentTypesService: DeploymentTypesService) {}

  ngOnInit(): void {
    this.getDeploymentTypes();
    let pillArea = document.querySelector('#size-selector-pills');

    pillArea.addEventListener("click", () => {

      let pills = pillArea.querySelectorAll(".pill");

      pills.forEach(pillDOM => {
        let selected_deploymentType = this.deploymentTypes.filter(dp => dp.displayName === pillDOM.innerHTML)[0];
        let selected_elements: any = Array.prototype.slice.call(document.getElementsByClassName(selected_deploymentType.sizeKey));

        if (pillDOM.classList.contains("unselected")) {
          for (let i = 0; i < selected_elements.length; i++) {
            let element = selected_elements[i];
            element.classList.add('size-display-hidden');
          }
        } else {
          for (let i = 0; i < selected_elements.length; i++) {
            let element = selected_elements[i];
            element.classList.remove('size-display-hidden');

          }
        }
      });
    });
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        this.deploymentTypes = data;
        this.deploymentTypes = this.deploymentTypes.filter(deploymentType => deploymentType.referenceDeploymentNames.length > 0);
        this.deploymentTypes.sort((first, second) => first.order > second.order);


        setTimeout(() => {
          let pills = document.querySelectorAll('#size-selector-pills .pill');

          Array.from(pills).forEach((pillDOM: Element) => {
            const pill = new Pill(<HTMLElement>pillDOM);
            pill.init();
          });
        }, 1000)

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

}
