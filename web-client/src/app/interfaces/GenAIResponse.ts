import { Question } from './Question';

export interface GenAIResponse {
  questions: Question[];
  title: string;
}
