import {Component, Input} from '@angular/core';

@Component({
  selector: '[app-unit-formula-view]',
  templateUrl: './unit-formula-view.component.html',
  styleUrls: ['./unit-formula-view.component.css']
})
export class UnitFormulaViewComponent {

  @Input() title;
  @Input() scaleUnit;
  @Input() scaleUnit_compare;

  constructor() { }
}
