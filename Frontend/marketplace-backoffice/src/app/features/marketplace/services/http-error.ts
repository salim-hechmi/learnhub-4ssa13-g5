import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/marketplace.models';

const TITLES: Record<number, string> = {
  400: 'Requête invalide', 401: 'Non authentifié', 403: 'Accès refusé', 404: 'Introuvable',
  409: 'Action impossible', 503: 'Service indisponible'
};

/** Transforme l'erreur JSON standard du backend en message lisible. */
export function readApiError(err: unknown): { title: string; detail: string } {
  if (err instanceof HttpErrorResponse) {
    const body = err.error as Partial<ApiError> | null;
    if (err.status === 0) return { title: 'Service indisponible', detail: 'Impossible de joindre le serveur.' };
    const fields = body?.validationErrors ? ' ' + Object.values(body.validationErrors).join(' ') : '';
    return {
      title: TITLES[err.status] ?? body?.error ?? 'Erreur',
      detail: (body?.message ?? err.message) + fields
    };
  }
  return { title: 'Erreur', detail: String(err) };
}
