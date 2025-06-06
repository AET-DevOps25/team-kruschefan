import {
  Component,
  model,
  ModelSignal,
} from '@angular/core';
import { QuestionTypeCard } from '../../interfaces/QuestionTypeCard';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { FormQuestionComponent } from './form-question/form-question.component';
import { Question, QuestionType } from '../../interfaces/Question';

@Component({
  selector: 'forms-ai-form-editor',
  imports: [DragDropModule, FormQuestionComponent],
  templateUrl: './form-editor.component.html',
  styleUrl: './form-editor.component.css',
})
export class FormEditorComponent {
  public questions: ModelSignal<Question[]> = model.required<Question[]>();
  protected onDrop(
    event: CdkDragDrop<
      Question[],
      Question[] | QuestionTypeCard[],
      Question | QuestionTypeCard
    >,
  ): void {
    if (event.previousContainer.id === event.container.id) {
      moveItemInArray(
        this.questions(),
        event.previousIndex,
        event.currentIndex,
      );
    } else {
      this.questions.update((currentQuestions) => {
        const newQuestion: Question = {
          label: event.item.data.label,
          type: event.item.data.label as QuestionType,
        };
        return [...currentQuestions, newQuestion];
      });
    }
  }
}
