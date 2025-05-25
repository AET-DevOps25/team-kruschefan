import { Component, input, InputSignal } from '@angular/core';
import { QuestionTypeCard } from '../../interfaces/QuestionTypeCard';
import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';

@Component({
  selector: 'forms-ai-form-editor',
  imports: [DragDropModule],
  templateUrl: './form-editor.component.html',
  styleUrl: './form-editor.component.css',
})
export class FormEditorComponent {
  protected onDrop(event: CdkDragDrop<QuestionTypeCard[]>): void {
    // Handle the drop event
    console.log('Dropped items:', event);
  }
}
