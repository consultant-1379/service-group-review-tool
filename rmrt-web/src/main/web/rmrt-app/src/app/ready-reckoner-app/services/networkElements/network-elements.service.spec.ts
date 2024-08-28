import { TestBed } from '@angular/core/testing';

import { NetworkElementsService } from './network-elements.service';

describe('NetworkElementsService', () => {
  let service: NetworkElementsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NetworkElementsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
