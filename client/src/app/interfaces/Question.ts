export interface Question {
  label: string;
  type: QuestionType;
}

export enum QuestionType {
  TEXT = 'Text',
  RADIO = 'Radio',
  CHECKBOX = 'Checkbox',
  DATE = 'Date',
  NUMBER = 'Number',
  FILE = 'File',
  MULTIPLE_CHOICE = 'Multiple Choice',
  COMMENT = 'Comment',
  TEXT_BOX = 'Text Box',
  SINGLE_CHOICE = 'Single Choice',
  DROPDOWN = 'Dropdown',
}
