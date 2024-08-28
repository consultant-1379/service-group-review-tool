import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const apiRef = '/api/referenceDeployments';
const apiDep = '/api/deploymentDependantLoadDrivers';

@Injectable({
  providedIn: 'root'
})
export class ReferenceDeploymentsService {

  constructor(private http:HttpClient) { }

  getReferenceDeployments() {
    return this.http.get(apiRef)
  }

  createReferenceDeployment(referenceDeployment, deploymentLoadDrivers, copy=false, newName=null ) {
    let holder = {
      referenceDeployment,
      deploymentLoadDrivers,
      copy,
      newName
    }
    let body = JSON.stringify(holder);
    return this.http.post(apiRef, body, httpOptions);
  }

  updateReferenceDeployment(referenceDeployment, deploymentLoadDrivers, copy=false, newName=null) {
    let holder = {
      referenceDeployment,
      deploymentLoadDrivers,
      copy,
      newName
    }
    let body = JSON.stringify(holder);
    return this.http.put(apiRef, body, httpOptions);
  }

  deleteReferenceDeployment(referenceDeployment) {
    let params = new HttpParams().set('name', referenceDeployment.name);
    return this.http.delete(apiRef, {params: params});
  }
  getDeploymentLoadDrivers() {
    return this.http.get(apiDep)
  }

  createDeploymentLoadDriver(deploymentLoadDrivers) {
    let body = JSON.stringify(deploymentLoadDrivers);
    return this.http.post(apiDep, body, httpOptions);
  }

  updateDeploymentLoadDriver(deploymentLoadDrivers) {
    let body = JSON.stringify(deploymentLoadDrivers);
    return this.http.put(apiDep, body, httpOptions);
  }

  deleteDeploymentLoadDriver(deploymentLoadDriver) {
    let params = new HttpParams().set('name', deploymentLoadDriver.name);
    return this.http.delete(apiDep, {params: params});
  }
}
