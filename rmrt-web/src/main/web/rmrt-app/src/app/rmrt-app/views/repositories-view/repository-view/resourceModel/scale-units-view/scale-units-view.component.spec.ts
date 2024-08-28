import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScaleUnitsViewComponent } from './scale-units-view.component';

describe('ScaleUnitsViewComponent', () => {
  let component: ScaleUnitsViewComponent;
  let fixture: ComponentFixture<ScaleUnitsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScaleUnitsViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScaleUnitsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
