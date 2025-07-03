import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserManagementComponent } from './user-management.component';
import { Router } from '@angular/router';
import { MatTable } from '@angular/material/table';
import { Component, ViewChild } from '@angular/core';

// Dummy table component to mock MatTable
@Component({
  selector: 'mat-table',
  template: '',
})
class MockMatTable<T> {
  @ViewChild('templateTable') templateTable!: MatTable<T>;
  renderRows = jasmine.createSpy('renderRows');
}

// Router spy
const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

describe('UserManagementComponent', () => {
  let component: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserManagementComponent],
      providers: [{ provide: Router, useValue: routerSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(UserManagementComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
    component['templateTable'] = jasmine.createSpyObj('MatTable', [
      'renderRows',
    ]) as any;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to editor on editTemplate', () => {
    const id = '1';
    component['editTemplate'](id);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/editor', id]);
  });

  it('should navigate to response on viewFormResponse', () => {
    const id = '2';
    component['viewFormResponse'](id);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/response', id]);
  });

  it('should delete template and call renderRows', () => {
    const initialLength = component['savedTemplates'].length;
    const idToDelete = component['savedTemplates'][0].id;

    component['deleteTemplate'](idToDelete);

    expect(component['savedTemplates'].length).toBe(initialLength - 1);
    expect(
      component['savedTemplates'].find((t) => t.id === idToDelete),
    ).toBeUndefined();
    expect(component['templateTable'].renderRows).toHaveBeenCalled();
  });
});
