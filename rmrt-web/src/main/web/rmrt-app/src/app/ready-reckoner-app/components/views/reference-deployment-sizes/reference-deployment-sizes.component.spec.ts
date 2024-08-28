import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceDeploymentSizesComponent } from './reference-deployment-sizes.component';

describe('ReferenceDeploymentSizesComponent', () => {
  let component: ReferenceDeploymentSizesComponent;
  let fixture: ComponentFixture<ReferenceDeploymentSizesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceDeploymentSizesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReferenceDeploymentSizesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
