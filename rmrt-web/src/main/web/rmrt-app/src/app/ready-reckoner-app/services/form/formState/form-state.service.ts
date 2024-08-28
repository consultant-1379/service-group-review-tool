import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { FormGroup } from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class FormStateService {
  private formSubject = new BehaviorSubject(null);
  form$ = this.formSubject.asObservable();

  constructor() { }

  saveFormState(form: FormGroup) {
    this.formSubject.next(form);
  }
}
