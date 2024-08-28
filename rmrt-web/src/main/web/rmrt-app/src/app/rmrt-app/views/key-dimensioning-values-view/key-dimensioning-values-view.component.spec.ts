import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KeyDimensioningValuesViewComponent } from './key-dimensioning-values-view.component';

describe('KeyDimensioningValuesViewComponent', () => {
  let component: KeyDimensioningValuesViewComponent;
  let fixture: ComponentFixture<KeyDimensioningValuesViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KeyDimensioningValuesViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(KeyDimensioningValuesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
