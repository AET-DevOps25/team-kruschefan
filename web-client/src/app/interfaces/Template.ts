import { Question } from './Question';

export interface Template {
  id: string;
  title: string;
  questions: Question[];
}
