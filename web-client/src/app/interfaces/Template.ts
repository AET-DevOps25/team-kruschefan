import { Question } from './Question';

export interface Template {
  templateName: string;
  questions: Question[];
}

export interface TemplateResponse {
  id: string;
  templateName: string;
  questions: Question[];
  createdAt: string;
}

export interface TemplateResponseTableSummary {
  position: number;
  id: string;
  templateName: string;
  createdAt: string;
}
