import { Component } from '@angular/core';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { QuestionTypeCards } from '../../configs/question-type-cards.config';
import { ItemCardComponent } from './item-card/item-card.component';

@Component({
  selector: 'forms-ai-item-list',
  imports: [ItemCardComponent, DragDropModule],
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.scss',
})
export class ItemListComponent {
  public items = QuestionTypeCards;
}
