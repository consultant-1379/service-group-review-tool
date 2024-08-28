import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadDriversViewComponent } from './load-drivers-view.component';

describe('LoadDriversViewComponent', () => {
  let component: LoadDriversViewComponent;
  let fixture: ComponentFixture<LoadDriversViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadDriversViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadDriversViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
