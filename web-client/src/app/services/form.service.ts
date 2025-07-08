import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Form } from '../interfaces/Form';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FormService {
  private http = inject(HttpClient);

  public createForm(form: Form): Observable<Form> {
    return this.http.post<Form>('/form', form);
  }
  public getForms(): Observable<Form[]> {
    return this.http.get<Form[]>('/form');
  }
  public getFormById(id: string): Observable<Form> {
    return this.http.get<Form>(`/form/${id}`);
  }
  public getFormsResponses(): Observable<Form[]> {
    return this.http.get<Form[]>('/form/responses');
  }
  public submitForm(form: Form): Observable<Form> {
    return this.http.post<Form>(`/form/responses`, form);
  }
  public getFormResponsesById(id: string): Observable<Form> {
    return this.http.get<Form>(`/form/responses/${id}`);
  }
}
