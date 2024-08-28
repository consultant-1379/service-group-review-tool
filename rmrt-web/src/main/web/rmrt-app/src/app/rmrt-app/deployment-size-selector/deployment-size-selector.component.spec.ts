import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentSizeSelectorComponent } from './deployment-size-selector.component';

describe('DeploymentSizeSelectorComponent', () => {
  let component: DeploymentSizeSelectorComponent;
  let fixture: ComponentFixture<DeploymentSizeSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeploymentSizeSelectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentSizeSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
