import { TestBed } from '@angular/core/testing';

import { FormActionsService } from './form-actions.service';

describe('FormActionsService', () => {
  let service: FormActionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormActionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
