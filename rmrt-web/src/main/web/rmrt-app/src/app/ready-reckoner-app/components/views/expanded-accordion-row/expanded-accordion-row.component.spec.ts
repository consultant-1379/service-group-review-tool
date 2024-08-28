import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpandedAccordionRowComponent } from './expanded-accordion-row.component';

describe('ExpandedAccordionRowComponent', () => {
  let component: ExpandedAccordionRowComponent;
  let fixture: ComponentFixture<ExpandedAccordionRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExpandedAccordionRowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpandedAccordionRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
