import { Component, inject } from '@angular/core';
import {
  MatDialogTitle,
  MatDialogContent,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'forms-ai-form-export',
  imports: [MatDialogTitle, MatDialogContent, MatIcon],
  templateUrl: './form-export.component.html',
  styleUrl: './form-export.component.scss',
})
export class FormExportComponent {
  readonly dialogData: { formId: string } = inject<{ formId: string }>(
    MAT_DIALOG_DATA,
  );
}
