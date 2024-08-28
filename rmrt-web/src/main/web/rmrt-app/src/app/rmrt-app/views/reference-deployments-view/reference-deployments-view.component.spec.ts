import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceDeploymentsViewComponent } from './reference-deployments-view.component';

describe('ReferenceDeploymentsViewComponent', () => {
  let component: ReferenceDeploymentsViewComponent;
  let fixture: ComponentFixture<ReferenceDeploymentsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceDeploymentsViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReferenceDeploymentsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
