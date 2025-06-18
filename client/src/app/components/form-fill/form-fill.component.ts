import { Component, inject, OnInit } from '@angular/core';
import { Template } from '../../interfaces/Template';
import { QuestionType } from '../../interfaces/Question';
import { MatInputModule } from '@angular/material/input';
import {
  Form,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { provideNativeDateAdapter } from '@angular/material/core';

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
        required: false,
      },
      {
        id: 'question5',
        type: QuestionType.MULTIPLE_CHOICE,
        label: 'What are your hobbies?',
        options: ['Reading', 'Traveling', 'Cooking', 'Sports'],
        required: false,
      },
      {
        id: 'question6',
        type: QuestionType.DROPDOWN,
        label: 'Select your country',
        options: ['USA', 'Canada', 'UK', 'Australia'],
        required: false,
      },
      {
        id: 'question7',
        type: QuestionType.COMMENT,
        label: 'Any additional comments?',
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
  protected form: FormGroup = new FormGroup({});
  private formBuilder = inject(FormBuilder);

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
    const formData = this.form.value;
    console.log('Form submitted:', formData);
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
    console.log('Processed Data:', processedData);
  }

  private initializeForm(): void {
    this.template.questions.forEach((question) => {
      if (
        question.type === QuestionType.MULTIPLE_CHOICE &&
        question.options &&
        question.options.length > 0
      ) {
        const checkboxGroup = this.formBuilder.group({});
        question.options.forEach((_, index) => {
          checkboxGroup.addControl(`${index}`, this.formBuilder.control(false));
        });
        this.form.addControl(question.id, checkboxGroup);
      } else {
        this.form.addControl(
          question.id,
          this.formBuilder.control(question.defaultValue ?? '', {
            validators: question.required ? Validators.required : null,
          }),
        );
      }
    });
  }
}
