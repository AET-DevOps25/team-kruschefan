import { Template } from './Template';

export interface Form {
  id: string;
  template: Template;
  responses: Record<string, string | number | Date | string[]>;
}
