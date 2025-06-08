import { Component, model, ModelSignal } from '@angular/core';
import { QuestionTypeCard } from '../../interfaces/QuestionTypeCard';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { FormQuestionComponent } from './form-question/form-question.component';
import { Question, QuestionType } from '../../interfaces/Question';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'forms-ai-form-editor',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    DragDropModule,
    FormQuestionComponent,
  ],
  templateUrl: './form-editor.component.html',
  styleUrl: './form-editor.component.scss',
})
export class FormEditorComponent {
  public questions: ModelSignal<Question[]> = model.required<Question[]>();
  protected onDrop(
    event: CdkDragDrop<Question[], Question[], Question | QuestionTypeCard>,
  ): void {
    if (event.previousContainer.id === event.container.id) {
      moveItemInArray(
        this.questions(),
        event.previousIndex,
        event.currentIndex,
      );
    } else {
      this.questions.update((currentQuestions) => {
        let newQuestion: Question = {
          label: event.item.data.label,
          type: event.item.data.label as QuestionType,
        };
        if ([QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE, QuestionType.DROPDOWN].includes(event.item.data.label as QuestionType)) {
          newQuestion = {
            ...newQuestion,
            options: ['Option 1', 'Option 2', 'Option 3'],
          };
        }
        const index = event.currentIndex;
        currentQuestions.splice(index, 0, newQuestion);
        return currentQuestions;
      });
    }
  }

  protected deleteQuestion(index: number): void {
    this.questions.update((currentQuestions) => {
      currentQuestions.splice(index, 1);
      return currentQuestions;
    });
  }
}
