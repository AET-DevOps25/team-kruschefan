import { Component } from '@angular/core';
import { ItemListComponent } from './components/item-list/item-list.component';

@Component({
  selector: 'forms-ai-app-root',
  imports: [ItemListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Forms-AI';
}
