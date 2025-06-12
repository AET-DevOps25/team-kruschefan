import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { GenAIResponse } from '../interfaces/GenAIResponse';

@Injectable({
  providedIn: 'root',
})
export class GenAiService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  generateForm(prompt: string): Observable<GenAIResponse> {
    return this.http.post<GenAIResponse>(`${this.baseUrl}/generate_form`, {
      prompt: prompt,
    });
  }
}
