import {
  Component,
  inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { Template } from '../../interfaces/Template';
import { QuestionType } from '../../interfaces/Question';
import { MatInputModule } from '@angular/material/input';
import {
  AbstractControl,
  Form,
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
import { ActivatedRoute, Router } from '@angular/router';

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
  protected readonly template: Template = {
    id: 'template1',
    questions: [
      {
        id: 'question1',
        type: QuestionType.TEXT,
        label: 'What is your name?',
        options: [],
        required: false,
      },
      {
        id: 'question2',
        type: QuestionType.NUMBER,
        label: 'How old are you?',
        placeholder: 'Enter your age',
        options: [],
        required: true,
      },
      {
        id: 'question3',
        type: QuestionType.DATE,
        label: 'What is your birth date?',
        options: [],
        required: false,
      },
      {
        id: 'question4',
        type: QuestionType.SINGLE_CHOICE,
        label: 'What is your age',
        options: [
          'Under 18',
          '18-24',
          '25-34',
          '35-44',
          '45-54',
          '55-64',
          '65 or older',
        ],
        required: true,
      },
      {
        id: 'question5',
        type: QuestionType.MULTIPLE_CHOICE,
        label: 'What are your hobbies?',
        options: ['Reading', 'Traveling', 'Cooking', 'Sports'],
        required: true,
      },
      {
        id: 'question6',
        type: QuestionType.DROPDOWN,
        label: 'Select your country',
        options: ['USA', 'Canada', 'UK', 'Australia'],
        required: true,
      },
      {
        id: 'question7',
        type: QuestionType.COMMENT,
        label:
          'Note: Contact the support team if you have any issues while filling out the form.',
        options: [],
        required: false,
      },
      {
        id: 'question8',
        type: QuestionType.TEXT_BOX,
        label: 'Describe your experience with our service',
        options: [],
        required: false,
      },
    ],
  };
  protected readonly response: {
    [key: string]: string | number | Date | string[];
  } = {
    question1: 'John Doe',
    question2: 30,
    question3: new Date('1993-01-01'),
    question4: '25-34',
    question5: ['Reading', 'Traveling'],
    question6: 'USA',
    question7: '',
    question8:
      'I had a wonderful experience using your service. The interface was user-friendly, and I found everything I needed easily.',
  };
  protected form: FormGroup = new FormGroup({});
  protected isFormSubmitted: WritableSignal<boolean> = signal(false);
  protected isReadonly: boolean = false;
  private formBuilder = inject(FormBuilder);
  private activatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    if (this.activatedRoute.snapshot.data['readonly']) {
      this.isReadonly = true;
    }
    this.initializeForm();
  }

  protected getControl(questionId: string): FormControl {
    return this.form.get(questionId) as FormControl;
  }

  protected getCheckboxGroup(questionId: string): FormGroup {
    return this.form.get(questionId) as FormGroup;
  }

  protected onSubmit(): void {
    const processedData: { [key: string]: string | string[] | Date | number } =
      {};
    for (const question of this.template.questions) {
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
        processedData[question.id] = selectedOptions;
      } else {
        processedData[question.id] = this.getControl(question.id).value;
      }
    }
    this.isFormSubmitted.set(true);
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

  private initializeForm(): void {
    this.template.questions.forEach((question) => {
      if (
        question.type === QuestionType.MULTIPLE_CHOICE &&
        question.options &&
        question.options.length > 0
      ) {
        const checkboxGroup = this.formBuilder.group(
          {},
          { validators: question.required ? this.checkboxValidation : null },
        );
        const selectedOptions = this.response[question.id] || [];
        question.options.forEach((_, index) => {
          checkboxGroup.addControl(
            `${index}`,
            this.formBuilder.control(
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
              ? (this.response[question.id] ?? '')
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
}
