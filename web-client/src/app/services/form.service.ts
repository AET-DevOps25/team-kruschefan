import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Form, FormResponse, FormSubmission } from '../interfaces/Form';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FormService {
  private http = inject(HttpClient);
  private baseUrl = environment.formApiUrl;

  public createForm(form: Form): Observable<FormResponse> {
    return this.http.post<FormResponse>(`${this.baseUrl}/form`, form);
  }
  public getForms(): Observable<FormResponse[]> {
    return this.http.get<FormResponse[]>(`${this.baseUrl}/form`);
  }
  public getFormById(id: string): Observable<FormResponse> {
    return this.http.get<FormResponse>(`${this.baseUrl}/form/${id}`);
  }
  public getFormsResponses(): Observable<FormResponse[]> {
    return this.http.get<FormResponse[]>(`${this.baseUrl}/form/responses`);
  }
  public submitForm(form: FormSubmission): Observable<Form> {
    return this.http.post<Form>(`${this.baseUrl}/form/responses`, form);
  }
  public getFormResponsesById(id: string): Observable<FormResponse> {
    return this.http.get<FormResponse>(`${this.baseUrl}/form/responses/${id}`);
  }
}
