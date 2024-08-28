import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnitFormulaViewComponent } from './unit-formula-view.component';

describe('UnitFormulaViewComponent', () => {
  let component: UnitFormulaViewComponent;
  let fixture: ComponentFixture<UnitFormulaViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UnitFormulaViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UnitFormulaViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
