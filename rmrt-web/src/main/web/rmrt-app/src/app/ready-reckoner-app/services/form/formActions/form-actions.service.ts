/*
* This service will help components communicate upon form action events.
*
* Each form action is represented as an Observable and will emit a new value
* to indicate the next action to be performed by the subscribing component.
*
* i.e. when form is submitted, validation will trigger, etc.
* */


import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FormActionsService {

  constructor() { }

  submitForm: BehaviorSubject<boolean> = new BehaviorSubject(false);
  submitForm$ = this.submitForm.asObservable();

  importFormData: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  importFormData$ = this.importFormData.asObservable();

  exportData: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  exportData$ = this.exportData.asObservable();

  deletedRowOutput: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  deletedRowOutput$ = this.deletedRowOutput.asObservable();

  toggleFormSubmit(flag: boolean) {
    this.submitForm.next(flag);
  }


  triggerImportData() {
    this.importFormData.next(true);
  }


  toggleExportData(flag: boolean) {
    this.exportData.next(flag);
  }


  updateRowsByOutput(outputKey) {
    this.deletedRowOutput.next(outputKey);
  }

}
