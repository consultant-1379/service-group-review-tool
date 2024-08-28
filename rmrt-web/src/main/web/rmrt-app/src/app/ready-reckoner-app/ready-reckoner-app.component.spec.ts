import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadyReckonerAppComponent } from './ready-reckoner-app.component';

describe('ReadyReckonerAppComponent', () => {
  let component: ReadyReckonerAppComponent;
  let fixture: ComponentFixture<ReadyReckonerAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReadyReckonerAppComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadyReckonerAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
