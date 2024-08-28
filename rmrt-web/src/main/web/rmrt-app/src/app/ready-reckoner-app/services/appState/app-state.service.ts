/*
* This service will be responsible for updating the state of the application
* The state of the application will be important in order to display the
* heading of the page depending on the current activated route.
* */


import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AppComponentStateService {
  private state = new BehaviorSubject('form');
  state$ = this.state.asObservable();

  changeState(state: string) {
    this.state.next(state);
  }


}
