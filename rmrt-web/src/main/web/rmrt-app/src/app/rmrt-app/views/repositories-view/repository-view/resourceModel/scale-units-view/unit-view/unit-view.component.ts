import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';

@Component({
  selector: '[app-unit-view]',
  templateUrl: './unit-view.component.html',
  styleUrls: ['./unit-view.component.css']
})
export class UnitViewComponent implements OnInit {

  @Input() title;
  @Input() size;

  @Input() scaleUnit;
  @Input() scaleUnit_compare;

  public values;
  public instances;

  constructor() { }

  ngOnInit(): void {
    this.values = this.title + "_values";
    this.instances = this.title + "_instances";
  }

  delta(oldValue, newValue) {
    if(!oldValue) {
      oldValue = 0;
    }

    if(!newValue) {
      newValue = 0;
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
