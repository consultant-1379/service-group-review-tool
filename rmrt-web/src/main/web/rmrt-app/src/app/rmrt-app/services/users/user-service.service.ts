import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

const api = '/api/user';
const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {

  constructor(private http:HttpClient) {}

  getLoggedInUser() {
    return this.http.get(api);
  }

  createUser(repo) {
    let body = JSON.stringify(repo);
    return this.http.post(api, body, httpOptions);
  }

  updateUser(user) {
    let body = JSON.stringify(user);
    return this.http.put(api, body, httpOptions);
  }
  deleteUser(user) {
    let params = new HttpParams().set('username', user.username);
    return this.http.delete(api, {params: params});
  }

  getAllUsers() {
    return this.http.get(api + "/users");
  }
}
