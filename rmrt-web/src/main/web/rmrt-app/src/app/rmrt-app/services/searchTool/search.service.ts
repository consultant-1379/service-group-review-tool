import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const api = '/api/search';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http:HttpClient) { }

  searchByName(name: string) {
    return this.http.get(api + "/name/" + name);
  }

  searchByFormula(formula: string) {
    return this.http.get(api + "/formula/" + formula);
  }
}
