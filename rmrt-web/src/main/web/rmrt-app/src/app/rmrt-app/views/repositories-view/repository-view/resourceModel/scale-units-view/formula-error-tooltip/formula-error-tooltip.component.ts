import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-formula-error-tooltip',
  templateUrl: './formula-error-tooltip.component.html',
  styleUrls: ['./formula-error-tooltip.component.css']
})
export class FormulaErrorTooltipComponent implements OnInit {
  @Input() unit;
  @Input() name;

  constructor() { }

  ngOnInit(): void {
  }

}
