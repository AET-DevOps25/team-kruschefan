import { DragDropModule } from '@angular/cdk/drag-drop';
import { Component, input, InputSignal } from '@angular/core';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'forms-ai-form-question',
  imports: [MatInputModule, DragDropModule],
  templateUrl: './form-question.component.html',
  styleUrl: './form-question.component.css',
})
export class FormQuestionComponent {
  questionLabel: InputSignal<string> = input<string>('');
}
