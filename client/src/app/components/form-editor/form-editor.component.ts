import {
  Component,
  DestroyRef,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { QuestionTypeCard } from '../../interfaces/QuestionTypeCard';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import {v4 as uuid} from 'uuid';
import { FormQuestionComponent } from './form-question/form-question.component';
import { Question, QuestionType } from '../../interfaces/Question';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { GenAiService } from '../../services/gen-ai.service';
import { catchError, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ItemListComponent } from "../item-list/item-list.component";

interface GenAIResponse {
  questions: Question[];
  title: string;
}

@Component({
  selector: 'forms-ai-form-editor',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    DragDropModule,
    FormQuestionComponent,
    FormsModule,
    ItemListComponent
],
  templateUrl: './form-editor.component.html',
  styleUrl: './form-editor.component.scss',
})
export class FormEditorComponent {
  protected questions: WritableSignal<Question[]> = signal<Question[]>([]);
  protected prompt = signal<string>('');
  private genAiService = inject(GenAiService);
  private destroyRef = inject(DestroyRef);
  private readonly randomQuestions = [
    { label: 'What is your name?', type: QuestionType.TEXT },
    { label: 'What is your age?', type: QuestionType.NUMBER },
    {
      label: 'What is your favorite color?',
      type: QuestionType.SINGLE_CHOICE,
      options: ['Red', 'Blue', 'Green'],
    },
    {
      label: 'What are your hobbies?',
      type: QuestionType.MULTIPLE_CHOICE,
      options: ['Reading', 'Traveling', 'Gaming'],
    },
    {
      label: 'Please select your country',
      type: QuestionType.DROPDOWN,
      options: ['USA', 'Canada', 'UK', 'Australia'],
    },
  ];

  protected generateForm(): void {
    if (this.prompt().length === 0) {
      return;
    }
    this.genAiService
      .generateForm(this.prompt())
      .pipe(
        catchError((error) => {
          console.error('Error generating form:', error);
          return of({
            questions: this.randomQuestions,
            title: '',
          } as GenAIResponse); // Return an empty response on error
        }),
        tap((form: GenAIResponse) => this.questions.set(form.questions)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

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
          id: uuid(),
          label: '',
          type: event.item.data.label as QuestionType,
        };
        if (
          [
            QuestionType.SINGLE_CHOICE,
            QuestionType.MULTIPLE_CHOICE,
            QuestionType.DROPDOWN,
          ].includes(event.item.data.label as QuestionType)
        ) {
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
