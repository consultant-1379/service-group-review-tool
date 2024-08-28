import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { shareReplay } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class NetworkElementsService {

  private apiRef = '/api/NetworkElementTypes';
  private apiSupportedRef = '/api/NetworkElementTypes/supported';
  private apiPostRef = '/api/readyReckoner';
  private quickEvalRef = '/api/quickEval';

  private cache$: Observable<any>;

  constructor( private http:HttpClient ) { }

  //Make API request
  requestNetworkElementTypes() {
    return this.http.get( this.apiRef );
  }


  //check if there are network elements stored in the cache,
  //if there is data in the cache then return the data, otherwise make a call to api for data
  get networkElementTypes() {
    if( !this.cache$ ) {
      this.cache$ = this.requestNetworkElementTypes().pipe(
        shareReplay(1)
      );
    }

    return this.cache$;
  }


  calculateReferenceDeployments(networkElementInputs) {
    return this.http.post(this.apiPostRef, networkElementInputs);
  }


  getSupportedNETypes() {
    return this.http.get(this.apiSupportedRef);
  }


  evaluateOutput(requestBody) {
    return this.http.post(this.quickEvalRef, requestBody);
  }

}
