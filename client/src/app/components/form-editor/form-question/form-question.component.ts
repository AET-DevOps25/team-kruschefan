import { DragDropModule } from '@angular/cdk/drag-drop';
import {
  Component,
  input,
  InputSignal,
  output,
  OutputEmitterRef,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'forms-ai-form-question',
  imports: [MatInputModule, MatButtonModule, DragDropModule],
  templateUrl: './form-question.component.html',
  styleUrl: './form-question.component.scss',
})
export class FormQuestionComponent {
  questionLabel: InputSignal<string> = input<string>('');
  deleteClicked: OutputEmitterRef<void> = output<void>();

  protected deleteQuestion(): void {
    this.deleteClicked.emit();
  }
}
