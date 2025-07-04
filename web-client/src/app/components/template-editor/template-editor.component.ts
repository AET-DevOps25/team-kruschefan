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
import { v4 as uuid } from 'uuid';
import { TemplateQuestionComponent } from './template-question/template-question.component';
import { Question, QuestionType } from '../../interfaces/Question';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { GenAiService } from '../../services/gen-ai.service';
import { catchError, finalize, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ItemListComponent } from '../item-list/item-list.component';
import { MatDialog } from '@angular/material/dialog';
import { FormExportComponent } from '../dialogs/form-export/form-export.component';

interface GenAIResponse {
  questions: Question[];
  title: string;
}

@Component({
  selector: 'forms-ai-template-editor',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    DragDropModule,
    TemplateQuestionComponent,
    FormsModule,
    ItemListComponent,
    MatProgressSpinnerModule,
  ],
  templateUrl: './template-editor.component.html',
  styleUrl: './template-editor.component.scss',
})
export class TemplateEditorComponent {
  protected templateTitle: WritableSignal<string> = signal<string>('');
  protected questions: WritableSignal<Question[]> = signal<Question[]>([]);
  protected isFormGenerating: WritableSignal<boolean> = signal<boolean>(false);
  protected prompt = signal<string>('');
  private genAiService = inject(GenAiService);
  private destroyRef = inject(DestroyRef);
  private dialog = inject(MatDialog);
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

  protected openFormExportDialog(): void {
    this.dialog.open(FormExportComponent, {
      data: { formId: uuid() },
    });
  }

  protected generateForm(): void {
    if (this.prompt().length === 0) {
      return;
    }
    this.isFormGenerating.set(true);
    this.genAiService
      .generateForm(this.prompt())
      .pipe(
        catchError((error) => {
          console.error('Error generating form:', error);
          return of({
            questions: this.randomQuestions,
            title: 'Introduction Form',
          } as GenAIResponse); // Return an empty response on error
        }),
        tap((form: GenAIResponse) => {
          this.questions.set(form.questions);
          this.templateTitle.set(form.title);
        }),
        finalize(() => {
          this.isFormGenerating.set(false);
        }),
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
