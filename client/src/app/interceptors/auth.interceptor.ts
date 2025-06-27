import { inject } from '@angular/core';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { from } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { KeycloakService } from '../services/keycloak.service';

export function authInterceptor(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
) {
  const keycloakService = inject(KeycloakService);

  if (keycloakService.IsLoggedIn) {
    return from(keycloakService.getToken()).pipe(
      mergeMap((token) => {
        if (token) {
          const authorizedRequest = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`,
            },
          });
          return next(authorizedRequest);
        }
        return next(request);
      }),
    );
  }
  return next(request);
}
