import { TestBed } from '@angular/core/testing';

import { FormControlsService } from './form-controls.service';

describe('FormControlsService', () => {
  let service: FormControlsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormControlsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
