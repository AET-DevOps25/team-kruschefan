import { Component, inject, ViewChild, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTable, MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { TemplateService } from '../../services/template.service';
import { catchError, EMPTY, take } from 'rxjs';
import {
  FormCreatedTableSummary,
  FormResponseTableSummary,
} from '../../interfaces/Form';
import { TemplateResponseTableSummary } from '../../interfaces/Template';
import { FormService } from '../../services/form.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { DeleteConfirmationComponent } from '../dialogs/delete-confirmation/delete-confirmation.component';

@Component({
  selector: 'forms-ai-user-management',
  imports: [MatTableModule, MatButtonModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss',
})
export class UserManagementComponent implements OnInit {
  @ViewChild('templateTable')
  templateTable!: MatTable<TemplateResponseTableSummary>;
  protected readonly templateTableColumns: string[] = [
    'position',
    'templateName',
    'createdAt',
    'actions',
  ];
  protected readonly formCreatedTableColumns: string[] = [
    'position',
    'formName',
    'createdAt',
    'actions',
  ];
  protected readonly formResponseTableColumns: string[] = [
    'position',
    'formName',
    'submittedOn',
    'actions',
  ];
  protected savedTemplates: TemplateResponseTableSummary[] = [];
  protected submittedForms: FormResponseTableSummary[] = [];
  protected createdForms: FormCreatedTableSummary[] = [];
  private router = inject(Router);
  private templateService = inject(TemplateService);
  private formService = inject(FormService);
  private matSnackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    this._getTemplates();
    this._getCreatedForms();
    this._getFormResponses();
  }

  protected editTemplate(id: string): void {
    this.router.navigate(['/editor', id]);
  }
  protected requestTemplateDelete(id: string): void {
    const dialogRef = this.dialog.open(DeleteConfirmationComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteTemplate(id);
      }
    });
  }
  protected viewForm(id: string): void {
    this.router.navigate(['/form', id]);
  }

  private deleteTemplate(id: string): void {
    this.templateService
      .deleteTemplate(id)
      .pipe(
        catchError((error) => {
          console.error('Error deleting template:', error);
          this.matSnackBar.open(
            'Error deleting template. Please try again later.',
            'Close',
            {
              duration: 3000,
            },
          );
          return EMPTY;
        }),
        take(1),
      )
      .subscribe(() => {
        this._removeTemplateFromList(id);
        this.matSnackBar.open('Template deleted successfully.', 'Close', {
          duration: 3000,
        });
      });
  }
  private _removeTemplateFromList(id: string): void {
    if (!this.savedTemplates || this.savedTemplates.length === 0) {
      console.warn('No templates available to remove.');
      return;
    }
    if (!id) {
      console.error('Invalid template ID provided for removal.');
      return;
    }
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

  private _getTemplates(): void {
    this.templateService
      .getTemplates()
      .pipe(
        catchError((error) => {
          console.error('Error fetching templates:', error);
          return [];
        }),
        take(1),
      )
      .subscribe((templates) => {
        this.savedTemplates = templates.map((template, index) => {
          return {
            position: index + 1,
            id: template.id,
            templateName: template.templateName,
            createdAt: new Date().toDateString(),
          };
        });
      });
  }

  private _getCreatedForms(): void {
    this.formService
      .getForms()
      .pipe(
        catchError((error) => {
          console.error('Error fetching created forms:', error);
          return [];
        }),
        take(1),
      )
      .subscribe((forms) => {
        this.createdForms = forms.map((form, index) => {
          return {
            position: index + 1,
            formId: form.id,
            formName: form.formName,
            createdAt: new Date().toDateString(),
          };
        });
      });
  }

  private _getFormResponses(): void {
    this.formService
      .getFormsResponses()
      .pipe(
        catchError((error) => {
          console.error('Error fetching forms:', error);
          return [];
        }),
        take(1),
      )
      .subscribe((forms) => {
        this.submittedForms = forms.map((form, index) => {
          return {
            position: index + 1,
            id: form.id,
            formId: form.formId,
            formName: form.formName,
            submittedOn: new Date().toDateString(),
          };
        });
      });
  }
}
