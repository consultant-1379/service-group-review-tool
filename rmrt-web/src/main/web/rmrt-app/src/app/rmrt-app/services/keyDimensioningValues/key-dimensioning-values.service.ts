import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

const api = '/api/keyDimensioningValues';

@Injectable({
  providedIn: 'root'
})
export class KeyDimensioningValuesService {

  constructor(private http:HttpClient) { }

  getKeyDimensioningValues() {
    return this.http.get(api);
  }

}
