import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormulaErrorTooltipComponent } from './formula-error-tooltip.component';

describe('ForumlaErrorTooltipComponent', () => {
  let component: FormulaErrorTooltipComponent;
  let fixture: ComponentFixture<FormulaErrorTooltipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormulaErrorTooltipComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormulaErrorTooltipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
