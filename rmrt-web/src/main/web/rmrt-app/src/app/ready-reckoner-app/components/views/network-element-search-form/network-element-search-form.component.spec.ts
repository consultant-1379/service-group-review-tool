import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkElementSearchFormComponent } from './network-element-search-form.component';

describe('NetworkElementSearchComponent', () => {
  let component: NetworkElementSearchFormComponent;
  let fixture: ComponentFixture<NetworkElementSearchFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NetworkElementSearchFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkElementSearchFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
