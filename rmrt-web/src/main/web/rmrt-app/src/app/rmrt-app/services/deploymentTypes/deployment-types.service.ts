import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

const api = '/api/deploymentTypes';
const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class DeploymentTypesService {

  constructor(private http:HttpClient) {}

  getDeploymentTypes() {
    return this.http.get(api)
  }

  getAllDeploymentTypes() {
    return this.http.get(api + "/all")
  }

  createDeploymentType(deploymentType) {
    let body = JSON.stringify(deploymentType);
    return this.http.post(api, body, httpOptions);
  }

  updateDeploymentTypes(deploymentType) {
    let body = JSON.stringify(deploymentType);
    return this.http.put(api, body, httpOptions);
  }
  deleteDeploymentTypes(deploymentType) {
    let params = new HttpParams().set('deploymentType', deploymentType.sizeKey);
    return this.http.delete(api, {params: params});
  }
}
