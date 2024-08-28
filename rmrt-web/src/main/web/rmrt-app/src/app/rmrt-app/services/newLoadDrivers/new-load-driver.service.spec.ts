import { TestBed } from '@angular/core/testing';

import { NewLoadDriverService } from './new-load-driver.service';

describe('NewLoadDriverService', () => {
  let service: NewLoadDriverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NewLoadDriverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
