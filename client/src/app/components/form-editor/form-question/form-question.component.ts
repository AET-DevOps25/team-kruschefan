import { DragDropModule } from '@angular/cdk/drag-drop';
import {
  Component,
  input,
  InputSignal,
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
  ],
  templateUrl: './form-question.component.html',
  providers: [provideNativeDateAdapter()],
  styleUrl: './form-question.component.scss',
})
export class FormQuestionComponent {
  question: InputSignal<Question> = input.required<Question>();
  deleteClicked: OutputEmitterRef<void> = output<void>();
  Types = QuestionType;

  protected deleteQuestion(): void {
    this.deleteClicked.emit();
  }

}
