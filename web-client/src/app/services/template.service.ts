import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Template } from '../interfaces/Template';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TemplateService {
  private http = inject(HttpClient);

  public createTemplate(template: Template): Observable<Template> {
    return this.http.post<Template>('/template', template);
  }

  public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>('/template');
  }

  public getTemplateById(id: string): Observable<Template> {
    return this.http.get<Template>(`/template/${id}`);
  }

  public updateTemplate(template: Template): Observable<Template> {
    return this.http.put<Template>(`/template/${template.id}`, template);
  }

  public deleteTemplate(id: string): Observable<void> {
    return this.http.delete<void>(`/template/${id}`);
  }
}
