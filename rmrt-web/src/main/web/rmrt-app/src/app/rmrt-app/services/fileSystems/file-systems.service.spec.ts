import { TestBed } from '@angular/core/testing';

import { FileSystemsService } from './file-systems.service';

describe('FileSystemsService', () => {
  let service: FileSystemsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileSystemsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
