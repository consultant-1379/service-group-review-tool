import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryAdminComponent } from './repository-admin.component';

describe('RepositoryAdminComponent', () => {
  let component: RepositoryAdminComponent;
  let fixture: ComponentFixture<RepositoryAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepositoryAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
