import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { catchError, tap, throwError } from 'rxjs';

export interface ApiTrace { method: string; path: string; status: number; text: string; ms: number; }

/** Alimente l'« inspecteur HTTP » de l'en-tête (repris de l'interface commune du groupe). */
@Injectable({ providedIn: 'root' })
export class ApiTraceService {
  readonly last = signal<ApiTrace>({ method: 'GET', path: '/api/orders/stats', status: 200, text: 'OK', ms: 0 });
}

const REASONS: Record<number, string> = {
  200: 'OK', 201: 'Created', 204: 'No Content', 400: 'Bad Request', 401: 'Unauthorized', 402: 'Payment Required',
  403: 'Forbidden', 404: 'Not Found', 409: 'Conflict', 500: 'Server Error', 503: 'Unavailable', 0: 'Network Error'
};

export const apiTraceInterceptor: HttpInterceptorFn = (req, next) => {
  if (!/\/api\/(orders|payments|invoices)/.test(req.url)) return next(req);
  const trace = inject(ApiTraceService);
  const t0 = performance.now();
  const path = req.urlWithParams.replace(/^https?:\/\/[^/]+/, '');
  return next(req).pipe(
    tap(ev => {
      if (ev instanceof HttpResponse) {
        trace.last.set({ method: req.method, path, status: ev.status, text: REASONS[ev.status] ?? '', ms: Math.round(performance.now() - t0) });
      }
    }),
    catchError(err => {
      trace.last.set({ method: req.method, path, status: err.status, text: REASONS[err.status] ?? 'Error', ms: Math.round(performance.now() - t0) });
      return throwError(() => err);
    })
  );
};
