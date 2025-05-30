import { Component } from '@angular/core';
import { QuestionTypeCards } from '../../configs/question-type-cards.config';
import { ItemCardComponent } from './item-card/item-card.component';

@Component({
  selector: 'forms-ai-item-list',
  imports: [ItemCardComponent],
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.css',
})
export class ItemListComponent {
  public items = QuestionTypeCards;
}
