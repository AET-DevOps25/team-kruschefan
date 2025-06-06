import { Component, signal, WritableSignal } from '@angular/core';
import { ItemListComponent } from './components/item-list/item-list.component';
import { FormEditorComponent } from './components/form-editor/form-editor.component';
import { Question, QuestionType } from './interfaces/Question';

@Component({
  selector: 'forms-ai-app-root',
  imports: [ItemListComponent, FormEditorComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Forms-AI';
  questions: WritableSignal<Question[]> = signal<Question[]>([
    { label: 'string', type: QuestionType.TEXT },
  ]);
}
