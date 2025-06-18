import { Question } from './Question';

export interface Template {
  id: string;
  questions: Question[];
}
