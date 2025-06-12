import { DragDropModule } from '@angular/cdk/drag-drop';
import {
  Component,
  DestroyRef,
  inject,
  model,
  output,
  OutputEmitterRef,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { Question, QuestionType } from '../../../interfaces/Question';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { PropertiesEditorComponent } from '../../dialogs/properties-editor/properties-editor.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'forms-ai-form-question',
  imports: [
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDatepickerModule,
    MatIconModule,
    MatSelectModule,
    DragDropModule,
    FormsModule,
  ],
  templateUrl: './form-question.component.html',
  providers: [provideNativeDateAdapter()],
  styleUrl: './form-question.component.scss',
})
export class FormQuestionComponent {
  question = model.required<Question>();
  deleteClicked: OutputEmitterRef<void> = output<void>();
  readonly dialog = inject(MatDialog);
  readonly destroyRef = inject(DestroyRef);
  Types = QuestionType;

  protected openPropertiesEditor(): void {
    this.dialog
      .open(PropertiesEditorComponent, {
        data: {
          ...this.question(),
          options: this.question().options ? [...this.question().options!] : [],
        },
        width: '400px',
      })
      .afterClosed()
      .pipe(
        tap((result) => {
          if (result) {
            this.question().label = result.label;
            this.question().required = result.required;
            this.question().defaultValue = result.defaultValue;
            this.question().placeholder = result.placeholder;
            this._copyOptions(result.options);
          }
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

  protected deleteQuestion(): void {
    this.deleteClicked.emit();
  }

  private _copyOptions(options: string[] | undefined): void {
    if (options && this.question().options) {
      const minOptions = Math.min(
        this.question().options!.length || 0,
        options.length,
      );
      for (let i = 0; i < minOptions; i++) {
        this.question().options![i] = options[i];
      }
      if (options.length > minOptions) {
        this.question().options!.push(...options.slice(minOptions));
      } else {
        this.question().options!.splice(minOptions);
      }
    }
  }
}
