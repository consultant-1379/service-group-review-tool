import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const api = '/api/newLoadDrivers';

@Injectable({
  providedIn: 'root'
})
export class NewLoadDriverService {

  constructor(private http:HttpClient) { }

  createNewLoadDrivers(newLoadDrivers) {
    let body = JSON.stringify(newLoadDrivers);
    return this.http.post(api, body, httpOptions);
  }
}
