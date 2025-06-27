import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'forms-ai-home',
  imports: [MatButtonModule, MatIconModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  private router = inject(Router);
  private keyCloakService = inject(KeycloakService);

  protected isAuthenticated = signal(this.keyCloakService.IsLoggedIn);

  protected login(): void {
    this.keyCloakService.login();
  }

  protected createNewTemplate(): void {
    this.router.navigate(['/editor']);
  }
}
