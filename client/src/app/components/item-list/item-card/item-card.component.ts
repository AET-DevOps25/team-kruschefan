import { Component, input, InputSignal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'forms-ai-item-card',
  imports: [MatCardModule],
  templateUrl: './item-card.component.html',
  styleUrl: './item-card.component.scss',
})
export class ItemCardComponent {
  label: InputSignal<string> = input.required<string>();
  imgSrc: InputSignal<string> = input.required<string>();
}
