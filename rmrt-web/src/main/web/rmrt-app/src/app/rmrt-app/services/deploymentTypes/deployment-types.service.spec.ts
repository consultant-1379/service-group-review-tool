import { TestBed } from '@angular/core/testing';

import { DeploymentTypesService } from './deployment-types.service';

describe('DeploymentTypesService', () => {
  let service: DeploymentTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeploymentTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
