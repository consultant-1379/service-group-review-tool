import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkSelectComponent } from './network-select.component';

describe('NetworkSelectComponent', () => {
  let component: NetworkSelectComponent;
  let fixture: ComponentFixture<NetworkSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NetworkSelectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
