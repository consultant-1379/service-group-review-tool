import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileSystemsAdminComponent } from './file-systems-admin.component';

describe('FileSystemsAdminComponent', () => {
  let component: FileSystemsAdminComponent;
  let fixture: ComponentFixture<FileSystemsAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileSystemsAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileSystemsAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
