import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-parameters-view',
  templateUrl: './parameters-view.component.html',
  styleUrls: ['./parameters-view.component.css']
})
export class ParametersViewComponent implements OnInit {

  @Input() parameters;

  constructor() {}

  ngOnInit(): void {
  }

  sorted(listOfObj) {
    return listOfObj.sort((a, b) => (a.order > b.order) ? 1 : -1);
  }
}
