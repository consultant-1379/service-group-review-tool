import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-load-drivers-view',
  templateUrl: './load-drivers-view.component.html',
  styleUrls: ['./load-drivers-view.component.css']
})
export class LoadDriversViewComponent implements OnInit {

  @Input() loadDrivers;

  constructor() { }

  ngOnInit(): void {
  }
  sorted(listOfObj) {
    return listOfObj.sort((a, b) => (a.order > b.order) ? 1 : -1);
  }

}
