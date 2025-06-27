export interface Question {
  id: string;
  label: string;
  type: QuestionType;
  options?: string[];
  defaultValue?: string | number | Date | string[];
  placeholder?: string;
  required?: boolean;
}

export enum QuestionType {
  TEXT = 'Text',
  DATE = 'Date',
  NUMBER = 'Number',
  MULTIPLE_CHOICE = 'Multiple Choice',
  COMMENT = 'Comment',
  TEXT_BOX = 'Text Box',
  SINGLE_CHOICE = 'Single Choice',
  DROPDOWN = 'Dropdown',
}
