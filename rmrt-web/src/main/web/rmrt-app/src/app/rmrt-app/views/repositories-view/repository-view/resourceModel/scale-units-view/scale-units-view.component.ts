import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-scale-units-view',
  templateUrl: './scale-units-view.component.html',
  styleUrls: ['./scale-units-view.component.css']
})
export class ScaleUnitsViewComponent implements OnInit {

  @Input() scaleUnit;
  @Input() scaleUnit_compare;
  @Input() deploymentTypes;

  public referenceDeployments

  constructor() {
  }

  ngOnInit(): void {

    function getSizes(unit_values) {
      if (unit_values) {
        let sizes = Object.keys(unit_values);
        if (sizes.length > 0) {
          return sizes.sort();
        }
      }
    }

    let options = [ this.scaleUnit.optimalUnit.conversion,  this.scaleUnit.minimumUnit.conversion,
                    this.scaleUnit.optimalUnit.profile,     this.scaleUnit.minimumUnit.profile]
    let params = ["cpuCores_values", "memory_values", "cpuMinutes_values", "peakCpuMinutes_values"];
    TopLoop:
    for(let index in options) {
      let unit = options[index];
      for(let jIndex in params) {
        let param = params[jIndex];
        let sizes = getSizes(unit[param])
        if(sizes) {
          this.referenceDeployments = sizes;
          break TopLoop;
        }
      }
    }
  }

}
