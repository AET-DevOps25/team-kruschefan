import { Question } from './Question';

export interface Form {
  formName: string;
  questions: Question[];
}

export interface FormResponse {
  id: string;
  formId: string;
  formName: string;
  questions: Question[];
  answers: FormQuestionAnswer[];
  submittedOn: string;
}

export interface FormQuestionAnswer {
  questionId: string;
  answer: string | number | Date | string[];
}

export interface FormSubmission {
  formId: string;
  answers: FormQuestionAnswer[];
}

export interface FormCreatedTableSummary {
  position: number;
  id: string;
  formId: string;
  formName: string;
  createdAt: string;
}

export interface FormResponseTableSummary {
  position: number;
  id: string;
  formName: string;
  formId: string;
  submittedOn: string;
}
