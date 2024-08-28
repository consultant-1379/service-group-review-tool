import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewLoadDriversDialogComponent } from './new-load-drivers-dialog.component';

describe('NewLoadDriversDialogComponent', () => {
  let component: NewLoadDriversDialogComponent;
  let fixture: ComponentFixture<NewLoadDriversDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewLoadDriversDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewLoadDriversDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
