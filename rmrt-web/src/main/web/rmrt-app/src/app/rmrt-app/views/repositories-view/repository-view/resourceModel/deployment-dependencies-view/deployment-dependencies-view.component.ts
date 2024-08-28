import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-deployment-dependencies-view',
  templateUrl: './deployment-dependencies-view.component.html',
  styleUrls: ['./deployment-dependencies-view.component.css']
})
export class DeploymentDependenciesViewComponent {

  @Input() deploymentDependencies;
  @Input() deploymentDependencies_compare;
  @Input() deploymentTypes;
  @Input() showZeroDeltas;

  constructor() {
  }

  sorted(listOfObj) {
    return listOfObj.sort((a, b) => (a.order > b.order) ? 1 : -1);
  }
  sortedWithZeroDeltaOption(deploymentDependency) {
    let ldcs= this.sorted(deploymentDependency.loadConversionFormulae);

    if(this.showZeroDeltas) {
      return ldcs;
    } else {
      return ldcs.filter(ldc => {
        let sameLdc = this.findSameLdc(deploymentDependency, ldc);

        // has an error show
        if (ldc.error) {
          return true;
        }

        // is not a formula show if not the same
        if (!ldc.isFormula) {
          return ldc.formula !== sameLdc.formula;
        }

        // is not zero delta show
        let notZero = false;
        this.deploymentTypes.forEach(deploymentType => {
          deploymentType.referenceDeploymentNames.forEach(refName => {
            let deltaValue = this.delta(ldc, sameLdc, refName);
            if (deltaValue !== 0) {
              notZero = true;
            }
          })
        });
        return notZero;
      });
    }


    // filter remove any with zero delta for all deployment types
  }

  findSameDependency(depSelected) {
    return this.deploymentDependencies_compare.filter(dep => dep.alias == depSelected.alias)[0];
  }

  findSameLdc(depSelected, ldcSelected) {
    let sameDep = this.findSameDependency(depSelected);
    if(sameDep) {
      let sameLdc = sameDep.loadConversionFormulae.filter(ldc => ldc.name == ldcSelected.name)[0];
      if(sameLdc) {
        return sameLdc;
      }
    }
    return {isFormula: ldcSelected.isFormula, values:{}}
  }

  delta(ldc: any, sameLdc: any, name) {
    let newValue = 0;
    let oldValue = 0;

    if(ldc.values && ldc.values[name]) {
      newValue = ldc.values[name];
    }

    if(sameLdc.values && sameLdc.values[name]) {
      oldValue = sameLdc.values[name];
    }

    return this.formatValue(newValue - oldValue);
  }

  formatValue(value) {
    if (isNaN(value) || !isFinite(value) || value === 0) {
      return 0;
    } else {
      try {
        let x = Number(value);
        return Number(x.toPrecision(6));
      } catch (e) {
        return 0;
      }
    }
  }



}
