import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

const api = '/api/repositories';

@Injectable({
  providedIn: 'root'
})
export class RepositoriesService {

  constructor(private http:HttpClient) {}

  getRepositories() {
    return this.http.get(api)
  }

  getRepository(project: string) {
    let params = new HttpParams().set('project', project);
    return this.http.get(api, { params: params });
  }

  createRepository(repo) {
    let body = JSON.stringify(repo);
    return this.http.post(api, body, httpOptions);
  }

  updateRepository(referenceDeployment) {
    let body = JSON.stringify(referenceDeployment);
    return this.http.put(api, body, httpOptions);
  }
  deleteRepository(repo) {
    let params = new HttpParams().set('project', repo.project);
    return this.http.delete(api, {params: params});
  }


}
