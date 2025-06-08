export interface Question {
  label: string;
  type: QuestionType;
  options?: string[];
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
