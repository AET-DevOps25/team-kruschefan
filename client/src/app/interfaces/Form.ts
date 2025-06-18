import { Template } from './Template';

export interface Form {
  id: string;
  template: Template;
  responses: { [questionId: string]: string | number | Date | string[] };
}
