import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Template, TemplateResponse } from '../interfaces/Template';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TemplateService {
  private http = inject(HttpClient);
  private baseUrl = environment.templateApiUrl;

  public createTemplate(template: Template): Observable<TemplateResponse> {
    return this.http.post<TemplateResponse>(
      `${this.baseUrl}/template`,
      template,
    );
  }

  public getTemplates(): Observable<TemplateResponse[]> {
    return this.http.get<TemplateResponse[]>(`${this.baseUrl}/template`);
  }

  public getTemplateById(id: string): Observable<TemplateResponse> {
    return this.http.get<TemplateResponse>(`${this.baseUrl}/template/${id}`);
  }

  public updateTemplate(
    id: string,
    template: Template,
  ): Observable<TemplateResponse> {
    return this.http.put<TemplateResponse>(
      `${this.baseUrl}/template/${id}`,
      template,
    );
  }

  public deleteTemplate(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/template/${id}`);
  }
}
