/*
* Progress bar view for showing results are being calculated when
* form is submitted.
* */

import { Component, OnInit } from '@angular/core';
import { AppComponentStateService } from "../../../services/appState/app-state.service";

@Component({
  selector: 'app-loading-results-progress',
  templateUrl: './loading-results-progress.component.html',
  styleUrls: ['./loading-results-progress.component.css']
})
export class LoadingResultsProgressComponent implements OnInit {

  public current_time;
  public max_time;

  constructor(private appStateService: AppComponentStateService) {
  }

  ngOnInit() {
    setTimeout(() => {
      this.appStateService.changeState('loading');
    }, 0);

    this.current_time = 0;
    this.max_time = 30; // seconds
    let intervalFn;

    intervalFn = setInterval( () => {
      this.current_time++;

      if(this.current_time > this.max_time) {
        clearInterval(intervalFn);
      }
    }, 1000);
  }
}
