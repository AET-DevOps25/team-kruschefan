import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'forms-ai-header',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  private authService = inject(KeycloakService);
  isAuthenticated = signal(this.authService.IsLoggedIn);

  login(): void {
    this.authService.login();
  }
  logout(): void {
    this.authService.logout();
  }
}
