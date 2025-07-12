import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogRef,
  MatDialogContent,
  MatDialogTitle,
  MatDialogActions,
} from '@angular/material/dialog';

@Component({
  selector: 'forms-ai-delete-confirmation',
  imports: [
    MatDialogContent,
    MatDialogActions,
    MatDialogContent,
    MatDialogActions,
    MatDialogTitle,
    MatButtonModule,
  ],
  templateUrl: './delete-confirmation.component.html',
  styleUrl: './delete-confirmation.component.scss',
})
export class DeleteConfirmationComponent {
  private matDialogRef = inject(MatDialogRef<DeleteConfirmationComponent>);

  protected confirmDeletion(): void {
    this.matDialogRef.close(true);
  }
  protected cancelDeletion(): void {
    this.matDialogRef.close(false);
  }
}
