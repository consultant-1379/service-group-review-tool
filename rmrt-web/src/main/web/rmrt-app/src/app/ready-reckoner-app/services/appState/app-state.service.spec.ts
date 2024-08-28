import { TestBed } from '@angular/core/testing';

import { AppComponentStateService } from './app-state.service';

describe('BreadCrumbService', () => {
  let service: AppComponentStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppComponentStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
