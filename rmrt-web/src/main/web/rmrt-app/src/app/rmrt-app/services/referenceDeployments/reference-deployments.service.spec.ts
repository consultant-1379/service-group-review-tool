import { TestBed } from '@angular/core/testing';

import { ReferenceDeploymentsService } from './reference-deployments.service';

describe('ReferenceDeploymentsService', () => {
  let service: ReferenceDeploymentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReferenceDeploymentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
