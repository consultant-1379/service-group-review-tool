import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RmrtAppComponent } from './rmrt-app.component';

describe('RmrtAppComponent', () => {
  let component: RmrtAppComponent;
  let fixture: ComponentFixture<RmrtAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RmrtAppComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RmrtAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
