import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private keycloak: Keycloak | undefined;

  async init(): Promise<boolean> {
    if (this.keycloak) {
      return true;
    }

    this.keycloak = new Keycloak({
      url: environment.keycloak.issuer,
      realm: environment.keycloak.realm,
      clientId: environment.keycloak.clientId,
    });

    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false,
      });
      return authenticated;
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      return false;
    }
  }

  get IsLoggedIn(): boolean {
    return !!this.keycloak?.authenticated;
  }

  get UserProfile(): Promise<Keycloak.KeycloakProfile> | undefined {
    return this.keycloak?.loadUserProfile();
  }

  get KeycloakInstance(): Keycloak | undefined {
    return this.keycloak;
  }

  login(): Promise<void> | undefined {
    return this.keycloak?.login();
  }

  logout(): Promise<void> | undefined {
    return this.keycloak?.logout({
      redirectUri: `${window.location.origin}/home`,
    });
  }

  getToken(): Promise<string | undefined> {
    return new Promise((resolve, reject) => {
      if (!this.keycloak || !this.keycloak.authenticated) {
        resolve(undefined);
        return;
      }
      this.keycloak
        .updateToken(30)
        .then(() => {
          resolve(this.keycloak?.token);
        })
        .catch((error) => {
          console.error('Failed to refresh token:', error);
          reject(error);
        });
    });
  }
}
