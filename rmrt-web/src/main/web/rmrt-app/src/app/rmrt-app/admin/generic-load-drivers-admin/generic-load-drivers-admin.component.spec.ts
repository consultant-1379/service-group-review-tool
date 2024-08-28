import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericLoadDriversAdminComponent } from './generic-load-drivers-admin.component';

describe('GenericLoadDriversAdminComponent', () => {
  let component: GenericLoadDriversAdminComponent;
  let fixture: ComponentFixture<GenericLoadDriversAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenericLoadDriversAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLoadDriversAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
