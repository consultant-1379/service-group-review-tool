import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const api = '/api/validation';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/xml'})
};

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  constructor(private http:HttpClient) { }

  postFile(file, project) {
    return this.http.post(api+"?project="+project, file, httpOptions);
  }

}
