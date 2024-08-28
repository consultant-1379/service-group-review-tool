import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentDependenciesViewComponent } from './deployment-dependencies-view.component';

describe('DeploymentDependenciesViewComponent', () => {
  let component: DeploymentDependenciesViewComponent;
  let fixture: ComponentFixture<DeploymentDependenciesViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeploymentDependenciesViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentDependenciesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
