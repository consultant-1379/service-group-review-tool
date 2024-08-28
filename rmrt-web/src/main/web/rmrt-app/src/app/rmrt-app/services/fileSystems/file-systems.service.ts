import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const api = '/api/fileSystems';

@Injectable({
  providedIn: 'root'
})
export class FileSystemsService {

  constructor(private http: HttpClient) {
  }

  getFileSystems() {
    return this.http.get(api);
  }

  getFileSystemNames() {
    return this.http.get(api + "/names");
  }

  createFileSystem(fileSystem) {
    let body = JSON.stringify(fileSystem);
    return this.http.post(api, body, httpOptions);
  }

  updateFileSystem(fileSystem) {
    let body = JSON.stringify(fileSystem);
    return this.http.put(api, body, httpOptions);
  }

  deleteFileSystem(fileSystem) {
    let params = new HttpParams()
      .set('physicalMapping', fileSystem.physicalMapping)
      .set('cloudMapping', fileSystem.cloudMapping);

    return this.http.delete(api, {params: params});
  }
}
