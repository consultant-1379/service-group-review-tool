import {Component, OnInit} from '@angular/core';
import {Accordion, Dialog, NotificationLog, TabGroup} from "@eds/vanilla";
import {SearchService} from "../../services/searchTool/search.service";
import {DeploymentTypesService} from "../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-search-dialog',
  templateUrl: './search-dialog.component.html',
  styleUrls: ['./search-dialog.component.css']
})
export class SearchDialogComponent implements OnInit {

  public deploymentTypes

  public nameResults;
  public formulaResults;

  private accordion;

  constructor(private searchService: SearchService, private deploymentTypesService: DeploymentTypesService) {}

  ngOnInit(): void {
    this.getDeploymentTypes();
    const dialogDOM = document.getElementById('search-dialog');

    if (dialogDOM) {
        const dialog = new Dialog(dialogDOM);
        dialog.init();
    }

    const tabDOM = document.getElementById('search-tabs');

    if (tabDOM) {
      const tabGroup = new TabGroup(tabDOM);
      tabGroup.init();
    }

    let nameInput:HTMLInputElement = <HTMLInputElement>document.getElementById("search-name");
    nameInput.onchange = () => {
      this.searchService.searchByName(nameInput.value).subscribe(
        data => {
          this.nameResults = data;

          setTimeout(()=> {
            if(!this.accordion) {
              let accordionDOM = document.getElementById("nameResultsAccordion");
              this.accordion = new Accordion(accordionDOM);
              this.accordion.init();
            }
          }, 300)

          console.log(data);
        },
        error => {
          console.log(error);
        },
        () => {}
      );
    }

    let formulaInput:HTMLInputElement = <HTMLInputElement>document.getElementById("search-formula");
    formulaInput.onchange = () => {
      this.searchService.searchByFormula(formulaInput.value).subscribe(
        data => {
          console.log(data);
          this.formulaResults = data;

        },
        error => {
          console.log(error);
        },
        () => {}
      );
    }
  }


  named(list) {
    let nameInput:HTMLInputElement = <HTMLInputElement>document.getElementById("search-name");
    let nameTerm:string = nameInput.value;

    let result = [];
    for(let obj of list) {
      let x: string = obj.name;
      if(x.toLowerCase().includes(nameTerm.toLowerCase())) {
        result.push(obj);
      }
    }
    return result;

  }


  used(list) {
    let formulaInput:HTMLInputElement = <HTMLInputElement>document.getElementById("search-formula");
    let formulaTerm:string = formulaInput.value;

    let result = [];
    for(let obj of list) {
      let x: string = obj.formula;
      if(x.toLowerCase().includes(formulaTerm.toLowerCase())) {
        result.push(obj);
      }
    }
    return result;
  }

  loadConversionFormulaeFormulaCount(repo) {
    let y = 0;
    for(let dep of repo.resourceModel.deploymentDependencies) {
      y += this.used(dep.loadConversionFormulae).length;
    }
    let x = 1 + y;
    return x == 1 ? 0 : x ;
  }

  repositoryFormulaRowCount(repo) {
    return 1 + this.loadConversionFormulaeFormulaCount(repo);
  }

  reposWithProperties(repositories: any) {
    return repositories.filter(repo => this.named(repo.resourceModel.properties).length > 0)
  }

  reposWithLoadDrivers(repositories: any) {
    return repositories.filter(repo => this.named(repo.resourceModel.loadDrivers).length > 0)
  }

  reposWithLoadConversionFormulae(repositories: any) {
    return repositories.filter(repo => {
      let listOfLists = repo.resourceModel.deploymentDependencies.map(dep => dep.loadConversionFormulae);
      let flatList = listOfLists.flatMap(x=>x)
      if(this.named(flatList).length > 0) return true;
    })
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        console.log(data);
        this.deploymentTypes = data;
        this.deploymentTypes = this.deploymentTypes.filter(deploymentType => deploymentType.referenceDeploymentNames.length > 0);

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
}
