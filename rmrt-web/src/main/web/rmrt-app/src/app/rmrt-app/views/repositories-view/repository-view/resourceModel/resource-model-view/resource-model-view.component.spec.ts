import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceModelViewComponent } from './resource-model-view.component';

describe('ResourceModelViewComponent', () => {
  let component: ResourceModelViewComponent;
  let fixture: ComponentFixture<ResourceModelViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResourceModelViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResourceModelViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
