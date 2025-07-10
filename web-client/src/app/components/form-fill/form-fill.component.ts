import {
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { QuestionType } from '../../interfaces/Question';
import { MatInputModule } from '@angular/material/input';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { provideNativeDateAdapter } from '@angular/material/core';
import { ActivatedRoute } from '@angular/router';
import { FormService } from '../../services/form.service';
import { FormQuestionAnswer, FormResponse } from '../../interfaces/Form';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'forms-ai-form-fill',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDatepickerModule,
    MatSelectModule,
    ReactiveFormsModule,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './form-fill.component.html',
  styleUrl: './form-fill.component.scss',
})
export class FormFillComponent implements OnInit {
  protected readonly Types = QuestionType;

  protected form: FormGroup = new FormGroup({});
  protected isFormSubmitted: WritableSignal<boolean> = signal(false);
  protected isReadonly = false;
  protected retrievedForm: FormResponse | null = null;
  private formResponse: FormQuestionAnswer[] = [];
  private formService = inject(FormService);
  private formBuilder = inject(FormBuilder);
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);
  private matSnackbar = inject(MatSnackBar);

  ngOnInit(): void {
    this.initializeForm();
  }

  protected getControl(questionId: string): FormControl {
    return this.form.get(questionId) as FormControl;
  }

  protected getCheckboxGroup(questionId: string): FormGroup {
    return this.form.get(questionId) as FormGroup;
  }

  protected onSubmit(): void {
    const processedData: FormQuestionAnswer[] = [];
    for (const question of this.retrievedForm!.questions) {
      if (
        question.type === QuestionType.MULTIPLE_CHOICE &&
        question.options &&
        question.options.length > 0
      ) {
        const selectedOptions: string[] = [];
        const checkboxGroup = this.getCheckboxGroup(question.id);
        for (const [key, control] of Object.entries(checkboxGroup.controls)) {
          if (control.value) {
            selectedOptions.push(question.options[Number(key)]);
          }
        }
        processedData.push({
          questionId: question.id,
          answer: selectedOptions,
        });
      } else {
        processedData.push({
          questionId: question.id,
          answer: this.getControl(question.id).value,
        });
      }
    }
    this.formService
      .submitForm({
        formId: this.retrievedForm!.id,
        answers: processedData,
      })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((error) => {
          console.error('Error submitting form:', error);
          this.matSnackbar.open('Error submitting form', 'Close', {
            duration: 3000,
            panelClass: ['mat-snackbar-error'],
          });
          return of(null);
        }),
      )
      .subscribe((response) => {
        if (response) {
          this.matSnackbar.open('Form submitted successfully', 'Close', {
            duration: 3000,
            panelClass: ['mat-snackbar-success'],
          });
          this.isFormSubmitted.set(true);
        }
      });
  }

  protected onReset(): void {
    this.form.reset();
    this.isFormSubmitted.set(false);
  }

  private checkboxValidation: ValidatorFn = (
    group: AbstractControl,
  ): ValidationErrors | null => {
    if (!(group instanceof FormGroup)) {
      return null;
    }
    const selectedOptions = Object.values(group.controls).filter(
      (control) => control.value,
    );
    return selectedOptions.length > 0 ? null : { required: true };
  };

  private fetchForm(formId: string): void {
    this.formService
      .getFormById(formId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((error) => {
          console.error('Error fetching form:', error);
          return of(null);
        }),
      )
      .subscribe((form: FormResponse | null) => {
        if (form) {
          this.retrievedForm = form;
          if (!this.isReadonly) {
            this.initializeFormControls();
          }
        } else {
          this.matSnackbar.open('Form not found', 'Close', {
            duration: 3000,
            panelClass: ['mat-snackbar-error'],
          });
        }
      });
  }

  private fetchFormResponse(responseId: string): void {
    this.formService
      .getFormResponsesById(responseId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((error) => {
          console.error('Error fetching form response:', error);
          return of(null);
        }),
      )
      .subscribe((response: FormResponse | null) => {
        if (response) {
          this.retrievedForm = response;
          this.formResponse = response.answers;
          this.initializeFormControls();
        } else {
          this.matSnackbar.open('Form response not found', 'Close', {
            duration: 3000,
            panelClass: ['mat-snackbar-error'],
          });
        }
      });
  }

  private initializeForm(): void {
    if (this.activatedRoute.snapshot.data['readonly']) {
      this.isReadonly = true;
    }
    if (!this.isReadonly) {
      const formId = this.activatedRoute.snapshot.params['formId'];
      if (!formId) {
        console.error('Form ID is not provided in the route parameters.');
        return;
      }
      this.fetchForm(formId);
    } else {
      const responseId = this.activatedRoute.snapshot.params['responseId'];
      if (this.isReadonly) {
        this.fetchFormResponse(responseId);
      }
    }
  }

  private initializeFormControls(): void {
    this.retrievedForm!.questions.forEach((question) => {
      if (
        question.type === QuestionType.MULTIPLE_CHOICE &&
        question.options &&
        question.options.length > 0
      ) {
        const checkboxGroup = this.formBuilder.group(
          {},
          { validators: question.required ? this.checkboxValidation : null },
        );
        const selectedOptions = this.getResponseByQuestionId(question.id) || [];
        question.options.forEach((_, index) => {
          checkboxGroup.addControl(
            `${index}`,
            this.formBuilder.control(
              this.isReadonly &&
                Array.isArray(selectedOptions) &&
                selectedOptions.includes(question.options![index]),
            ),
          );
        });
        this.form.addControl(question.id, checkboxGroup);
      } else {
        this.form.addControl(
          question.id,
          this.formBuilder.control(
            this.isReadonly
              ? (this.getResponseByQuestionId(question.id) ?? '')
              : (question.defaultValue ?? ''),
            {
              validators: question.required ? Validators.required : null,
            },
          ),
        );
      }
    });
    if (this.isReadonly) {
      this.form.disable();
    }
  }

  private getResponseByQuestionId(
    questionId: string,
  ): string | string[] | Date | number | undefined {
    return this.formResponse.find((answer) => answer.questionId === questionId)
      ?.answer;
  }
}
