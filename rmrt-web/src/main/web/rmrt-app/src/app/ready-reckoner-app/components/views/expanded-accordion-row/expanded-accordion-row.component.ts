import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-expanded-accordion-row',
  templateUrl: './expanded-accordion-row.component.html',
  styleUrls: ['./expanded-accordion-row.component.css']
})
export class ExpandedAccordionRowComponent implements OnInit {
  @Input() public mediationComponents;
  @Input() public keyDimensioningValues;

  constructor() { }

  ngOnInit(): void {
  }


  comparePercentages(percentage) {
    return ( percentage < 101 );
  }
}
