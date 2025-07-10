import {
  Component,
  DestroyRef,
  inject,
  OnInit,
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
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { GenAiService } from '../../services/gen-ai.service';
import { catchError, finalize, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ItemListComponent } from '../item-list/item-list.component';
import { MatDialog } from '@angular/material/dialog';
import { FormExportComponent } from '../dialogs/form-export/form-export.component';
import { TemplateService } from '../../services/template.service';
import { ActivatedRoute } from '@angular/router';
import { FormService } from '../../services/form.service';

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
export class TemplateEditorComponent implements OnInit {
  protected templateTitle: WritableSignal<string> = signal<string>('');
  protected questions: WritableSignal<Question[]> = signal<Question[]>([]);
  protected isFormGenerating: WritableSignal<boolean> = signal<boolean>(false);
  protected prompt = signal<string>('');
  private genAiService = inject(GenAiService);
  private templateService = inject(TemplateService);
  private formService = inject(FormService);
  private destroyRef = inject(DestroyRef);
  private dialog = inject(MatDialog);
  private router = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);
  private templateId = '';
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

  ngOnInit(): void {
    this.router.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
        const templateId = params['templateId'];
        if (templateId) {
          this.templateService
            .getTemplateById(templateId)
            .pipe(
              catchError((error) => {
                console.error('Error fetching template:', error);
                return of(null);
              }),
              takeUntilDestroyed(this.destroyRef),
            )
            .subscribe((template) => {
              if (template) {
                this.templateTitle.set(template.templateName);
                this.questions.set(template.questions);
                this.templateId = template.id;
              }
            });
        }
      });
  }

  protected openFormExportDialog(): void {
    this.formService
      .createForm({
        formName: this.templateTitle(),
        questions: this.questions(),
      })
      .pipe(
        catchError((error) => {
          console.error('Error creating form:', error);
          this.snackBar.open('Error creating form', 'Close', {
            duration: 3000,
          });
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((form) => {
        if (form) {
          this.snackBar.open('Form created successfully!', 'Close', {
            duration: 3000,
          });
          this.dialog.open(FormExportComponent, {
            data: { formId: form.id },
          });
        }
      });
  }

  protected saveTemplate(): void {
    if (this.templateId === '') {
      const template = {
        templateName: this.templateTitle(),
        questions: this.questions(),
      };
      this.templateService
        .createTemplate(template)
        .pipe(
          catchError((error) => {
            console.error('Error saving template:', error);
            return of(null);
          }),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((template) =>
          this.snackBar.open(
            template
              ? 'Template created successfully!'
              : 'Error creating template',
            'Close',
            {
              duration: 3000,
            },
          ),
        );
    } else {
      const template = {
        templateName: this.templateTitle(),
        questions: this.questions(),
      };
      this.templateService
        .updateTemplate(this.templateId, template)
        .pipe(
          catchError((error) => {
            console.error('Error updating template:', error);
            return of(null);
          }),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((template) => {
          this.snackBar.open(
            template ? 'Template saved successfully!' : 'Error saving template',
            'Close',
            {
              duration: 3000,
            },
          );
        });
    }
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
          } as GenAIResponse);
        }),
        tap((form: GenAIResponse) => {
          this.questions.set(
            form.questions.map((question) => ({
              ...question,
              id: uuid(),
              options: question.options || [],
              defaultValue: question.defaultValue || '',
              placeholder: question.placeholder || '',
              required: question.required || false,
            })),
          );
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
          // use enum key for type safety
          type: event.item.data.label as QuestionType,
          options: [],
          defaultValue: '',
          placeholder: '',
          required: false,
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
