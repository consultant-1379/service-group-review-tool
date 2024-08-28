import { TestBed } from '@angular/core/testing';

import { GenericLoadDriversService } from './generic-load-drivers.service';

describe('GenericLoadDriversService', () => {
  let service: GenericLoadDriversService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GenericLoadDriversService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
