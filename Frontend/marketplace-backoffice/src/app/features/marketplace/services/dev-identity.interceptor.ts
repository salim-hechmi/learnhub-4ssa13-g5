import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

/**
 * UNIQUEMENT pour tester le front contre marketplace-service en direct (:8085, profil standalone),
 * sans Gateway ni Keycloak : ajoute les en-têtes d'identité qu'ajouterait la Gateway.
 * En mode normal (environment.devIdentity = null) il ne fait rien.
 */
export const devIdentityInterceptor: HttpInterceptorFn = (req, next) => {
  const id = environment.devIdentity;
  if (!id || !req.url.includes('/api/')) return next(req);
  return next(req.clone({
    setHeaders: { 'X-User-Id': id.userId, 'X-User-Roles': id.roles, 'X-User-Email': id.email }
  }));
};
