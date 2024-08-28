import { TestBed } from '@angular/core/testing';

import { KeyDimensioningValuesService } from './key-dimensioning-values.service';

describe('KeyDimensioningValuesService', () => {
  let service: KeyDimensioningValuesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KeyDimensioningValuesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
