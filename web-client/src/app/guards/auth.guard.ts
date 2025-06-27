import { inject, Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { KeycloakService } from '../services/keycloak.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  private router = inject(Router);
  private keycloakService = inject(KeycloakService);

  canActivate(): boolean | UrlTree {
    const authenticated = this.keycloakService.IsLoggedIn;

    if (authenticated) {
      return true;
    } else {
      return this.router.createUrlTree(['/home']);
    }
  }
}
