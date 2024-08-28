import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceDeploymentsAdminComponent } from './reference-deployments-admin.component';

describe('ReferenceDeploymentsAdminComponent', () => {
  let component: ReferenceDeploymentsAdminComponent;
  let fixture: ComponentFixture<ReferenceDeploymentsAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceDeploymentsAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReferenceDeploymentsAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
