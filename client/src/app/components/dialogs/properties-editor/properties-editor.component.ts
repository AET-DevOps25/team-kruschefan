import { Component, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { Question, QuestionType } from '../../../interfaces/Question';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'forms-ai-properties-editor',
  imports: [
    MatButtonModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
  ],
  templateUrl: './properties-editor.component.html',
  providers: [provideNativeDateAdapter()],
  styleUrl: './properties-editor.component.scss',
})
export class PropertiesEditorComponent {
  readonly dialogRef = inject(MatDialogRef<PropertiesEditorComponent>);
  readonly question = inject<Question>(MAT_DIALOG_DATA);
  readonly Types = QuestionType;

  protected updateOption(index: number, value: string): void {
    if (!this.question.options) {
      this.question.options = [];
    }
    this.question.options[index] = value;
  }

  protected addOption(): void {
    if (!this.question.options) {
      this.question.options = [];
    }
    this.question.options.push('');
  }

  protected removeOption(index: number): void {
    if (this.question.options) {
      this.question.options.splice(index, 1);
    }
  }

  protected onCancel(): void {
    this.dialogRef.close();
  }
}
