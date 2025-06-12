import { Component } from '@angular/core';
import { ItemListComponent } from './components/item-list/item-list.component';
import { FormEditorComponent } from './components/form-editor/form-editor.component';

@Component({
  selector: 'forms-ai-app-root',
  imports: [ItemListComponent, FormEditorComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'Forms-AI';
}
