import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileSystemsViewComponent } from './file-systems-view.component';

describe('FileSystemsViewComponent', () => {
  let component: FileSystemsViewComponent;
  let fixture: ComponentFixture<FileSystemsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileSystemsViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileSystemsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
