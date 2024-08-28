import {Component, ViewEncapsulation} from '@angular/core';
import {RepositoriesService} from "../../../services/repositories/repositories.service";
import {ActivatedRoute} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {Accordion, NotificationLog} from "@eds/vanilla";
import {DeploymentTypesService} from "../../../services/deploymentTypes/deployment-types.service";

@Component({
  selector: 'app-repository-view',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './repository-view.component.html',
  styleUrls: ['./repository-view.component.css']
})
export class RepositoryViewComponent {

  public repository;
  public resourceModel;
  public compareModel;
  public showZeroDeltas = true;

  private name;
  public deploymentTypes;

  constructor(private repositoriesService: RepositoriesService,
              private deploymentTypesService: DeploymentTypesService,
              private route: ActivatedRoute,
              private titleService: Title) {
    this.route.queryParams.subscribe(params => {
      this.getDeploymentTypes();
      this.getRepository(params['project']);
      this.name = params['project'].split("/").pop();
      this.titleService.setTitle("RMRT - " + this.name.toUpperCase());
    });
  }

  getDeploymentTypes() {
    this.deploymentTypesService.getDeploymentTypes().subscribe(
      data => {
        console.log(data);
        this.deploymentTypes = data;
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving ' + this.name, // String
          description: 'Could not access from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        });
        console.log(err);
      },
      () => {
      }
    );
  }

  getRepository(project: string) {

    this.repositoriesService.getRepository(project).subscribe(
      data => {
        console.log(data);
        this.repository = data[0];
        if (this.repository.changeRequests.length > 0) {
          this.resourceModel = this.repository.changeRequests[0].resourceModel;
          this.compareModel = this.repository.resourceModel;
        } else {
          this.resourceModel = this.repository.resourceModel;
          this.compareModel = this.repository.resourceModel;
        }
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving ' + this.name, // String
          description: 'Could not access from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        });
        console.log(err);
      },
      () => {
        setTimeout(() => {
          const accordionDOM = document.getElementById('repository-accordion');

          const accordion = new Accordion(accordionDOM);
          accordion.init();

          console.log("Loaded " + project);
        }, 700);
      }
    );
  }

  getNewLoadDrivers() {
    for (let i = 0; i < this.repository.changeRequests.length; i++) {
      let change = this.repository.changeRequests[i];
      if (change.resourceModel === this.resourceModel) {

        change.newLoadDrivers.forEach(newLd => {
          if (!newLd.hasOwnProperty('generic')) {
            newLd.generic = false;
          }
          if (!newLd.hasOwnProperty('values')) {
            newLd.values = {}
          }
        });
        return change.newLoadDrivers;
      }
    }
    return [];
  }

  getFileUrl() {
    for (let i = 0; i < this.repository.changeRequests.length; i++) {
      let change = this.repository.changeRequests[i];
      if (change.resourceModel === this.resourceModel) {

        return "https://gerrit.ericsson.se/#/q/" + change.changeId;

      }
    }
    return `https://gerrit.ericsson.se/gitweb?a=blob;p=${this.repository.project}.git;f=${this.repository.filePath};`;
  }

  percentWithDelta(keyDimOrFileSys: any, ref: any) {
    try {
      let ldcs = this.getMatchingLdcs(keyDimOrFileSys);
      let delta = Number(this.delta(ldcs[0], ldcs[1], ref));
      let capacity = keyDimOrFileSys.capacities[ref];
      let total = keyDimOrFileSys.totals[ref];
      let percent = 100 * (total + delta) / capacity;
      return this.formatValue(percent);
    } catch (e) {
      console.log(e);
      return keyDimOrFileSys.percentages[ref];

    }
  }

  totalWithDelta(keyDimOrFileSys: any, ref: any) {
    try {
      let ldcs = this.getMatchingLdcs(keyDimOrFileSys);
      let delta = Number(this.delta(ldcs[0], ldcs[1], ref));
      let total = keyDimOrFileSys.totals[ref];
      return this.formatValue(total + delta);
    } catch (e) {
      console.log(e);
      return keyDimOrFileSys.totals[ref];
    }
  }

  getCapacity(keyDimOrFileSys: any, ref: any) {
    return this.formatValue(keyDimOrFileSys.capacities[ref]);
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

  noDeltasPresent() {
    for (let i = 0; i < this.repository.changeRequests.length; i++) {
      let change = this.repository.changeRequests[i];
      if (change.resourceModel === this.resourceModel) {
        return false;
      }
    }
    return true;
  }

  changeZeroDeltas() {
    this.showZeroDeltas = !this.showZeroDeltas;
  }


  getMatchingLdcs(keyOrFs) {
    if (keyOrFs.hasOwnProperty('name')) {
      // Key dim
      let name = keyOrFs.name;
      for (let i1 = 0; i1 < this.resourceModel.deploymentDependencies.length; i1++) {
        let dep = this.resourceModel.deploymentDependencies[i1];
        for (let i = 0; i < dep.loadConversionFormulae.length; i++) {
          let ldc = dep.loadConversionFormulae[i];
          if (ldc.name === name) {
            return [ldc, this.findSameLdc(dep, ldc)];
          }
        }
      }
    } else {
      // File sys
      let namesToMatch = [];
      namesToMatch.push(keyOrFs.physicalMapping);
      namesToMatch.push(keyOrFs.cloudMapping);
      keyOrFs.customMappings.forEach(custom => {
        namesToMatch.push(custom.trim());
      });

      for (let name in namesToMatch) {
        for (let i1 = 0; i1 < this.resourceModel.deploymentDependencies.length; i1++) {
          let dep = this.resourceModel.deploymentDependencies[i1];
          for (let i = 0; i < dep.loadConversionFormulae.length; i++) {
            let namedLdc = dep.loadConversionFormulae[i];
            if (namedLdc.formula === name) {
              for (let j = 0; j < dep.loadConversionFormulae.length; j++) {
                let valueLdc = dep.loadConversionFormulae[j];
                if(valueLdc.name.toLowerCase() === 'nasfilesystemspacemb') {
                  return [valueLdc, this.findSameLdc(dep, valueLdc)];
                }
              }
            }
          }
        }
      }
    }
  }

  findSameLdc(depSelected, ldcSelected) {
    let sameDep = this.compareModel.deploymentDependencies.filter(dep => dep.alias == depSelected.alias)[0];
    if (sameDep) {
      let sameLdc = sameDep.loadConversionFormulae.filter(ldc => ldc.name == ldcSelected.name)[0];
      if (sameLdc) {
        return sameLdc;
      }
    }
    return {isFormula: ldcSelected.isFormula, values: {}}
  }

  delta(ldc: any, sameLdc: any, name) {
    let newValue = 0;
    let oldValue = 0;

    if (ldc.values && ldc.values[name]) {
      newValue = ldc.values[name];
    }

    if (sameLdc.values && sameLdc.values[name]) {
      oldValue = sameLdc.values[name];
    }

    return this.formatValue(newValue - oldValue);
  }
}
