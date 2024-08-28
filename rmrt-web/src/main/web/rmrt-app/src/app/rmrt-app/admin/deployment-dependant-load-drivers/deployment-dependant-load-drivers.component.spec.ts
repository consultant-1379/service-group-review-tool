import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentDependantLoadDriversComponent } from './deployment-dependant-load-drivers.component';

describe('DeploymentDependantLoadDriversComponent', () => {
  let component: DeploymentDependantLoadDriversComponent;
  let fixture: ComponentFixture<DeploymentDependantLoadDriversComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeploymentDependantLoadDriversComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentDependantLoadDriversComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
