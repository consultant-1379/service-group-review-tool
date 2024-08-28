import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingResultsProgressComponent } from './loading-results-progress.component';

describe('LoadingResultsProgressComponent', () => {
  let component: LoadingResultsProgressComponent;
  let fixture: ComponentFixture<LoadingResultsProgressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadingResultsProgressComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadingResultsProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
