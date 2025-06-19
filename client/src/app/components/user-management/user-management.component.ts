import { Component, inject, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTable, MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';

interface Template {
  position: number;
  id: string;
  name: string;
  createdOn: string;
}

@Component({
  selector: 'forms-ai-user-management',
  imports: [MatTableModule, MatButtonModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss',
})
export class UserManagementComponent {
  @ViewChild('templateTable') templateTable!: MatTable<Template>;
  protected readonly templateTableColumns: string[] = [
    'position',
    'name',
    'createdOn',
    'actions',
  ];
  protected readonly formTableColumns: string[] = [
    'position',
    'name',
    'submittedOn',
    'actions',
  ];
  protected savedTemplates = [
    {
      position: 1,
      id: '1',
      name: 'Customer Feedback Form',
      createdOn: new Date().toDateString(),
    },
    {
      position: 2,
      id: '2',
      name: 'Business Registration Form',
      createdOn: new Date().toDateString(),
    },
    {
      position: 3,
      id: '3',
      name: 'Employee Onboarding Form',
      createdOn: new Date().toDateString(),
    },
  ];
  protected submittedForms = [
    {
      position: 1,
      id: '1',
      name: 'Customer Feedback Form',
      submittedOn: new Date().toDateString(),
    },
    {
      position: 2,
      id: '2',
      name: 'Customer Feedback Form',
      submittedOn: new Date().toDateString(),
    },
    {
      position: 3,
      id: '3',
      name: 'Employee Onboarding Form',
      submittedOn: new Date().toDateString(),
    },
  ];
  private router = inject(Router);

  protected editTemplate(id: string): void {
    this.router.navigate(['/editor', id]);
  }
  protected deleteTemplate(id: string): void {
    const index = this.savedTemplates.findIndex(
      (template) => template.id === id,
    );
    if (index > -1) {
      this.savedTemplates.splice(index, 1);
    }
    this.templateTable.renderRows();
  }
  protected viewFormResponse(id: string): void {
    this.router.navigate(['/response', id]);
  }
}
