import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

const api = '/api/genericLoadDrivers';

@Injectable({
  providedIn: 'root'
})
export class GenericLoadDriversService {

  constructor(private http:HttpClient) { }

  getGenericLoadDrivers() {
    return this.http.get(api);
  }


  createGenericLoadDriver(genericLoadDriver) {
    let body = JSON.stringify(genericLoadDriver);
    return this.http.post(api, body, httpOptions);
  }

  updateGenericLoadDriver(genericLoadDriver) {
    let body = JSON.stringify(genericLoadDriver);
    return this.http.put(api, body, httpOptions);
  }

  deleteGenericLoadDriver(genericLoadDriver) {
    let params = new HttpParams().set('name', genericLoadDriver.name);
    return this.http.delete(api, {params: params});
  }
}
