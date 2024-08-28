import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentTypesAdminComponent } from './deployment-types-admin.component';

describe('DeploymentTypesAdminComponent', () => {
  let component: DeploymentTypesAdminComponent;
  let fixture: ComponentFixture<DeploymentTypesAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeploymentTypesAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentTypesAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
