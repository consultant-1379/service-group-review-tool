import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericLoadDriversViewComponent } from './generic-load-drivers-view.component';

describe('GenericLoadDriversViewComponent', () => {
  let component: GenericLoadDriversViewComponent;
  let fixture: ComponentFixture<GenericLoadDriversViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenericLoadDriversViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLoadDriversViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
