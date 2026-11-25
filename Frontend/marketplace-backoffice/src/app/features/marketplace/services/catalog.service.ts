import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CatalogCourse { id: number; title: string; price: number; }

/**
 * Lecture du catalogue publié (course-service, via la Gateway) pour le sélecteur de cours.
 * Accepte une réponse paginée { content: [...] } ou un tableau. Si course-service est indisponible,
 * le formulaire permet d'ajouter un cours par son ID : le prix réel sera de toute façon
 * revérifié côté backend par OpenFeign.
 */
@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);
  readonly courses = signal<CatalogCourse[]>([]);
  readonly available = signal(true);

  load(): void {
    if (this.courses().length) return;
    this.http.get<unknown>(`${environment.apiGatewayUrl}/api/courses`, { params: { page: 0, size: 100 } }).pipe(
      map(res => {
        const arr = Array.isArray(res) ? res : ((res as { content?: unknown[] })?.content ?? []);
        return (arr as Record<string, unknown>[]).map(c => ({
          id: Number(c['id']), title: String(c['title'] ?? 'Cours #' + c['id']), price: Number(c['price'] ?? 0)
        }));
      }),
      catchError(() => { this.available.set(false); return of([] as CatalogCourse[]); })
    ).subscribe(list => this.courses.set(list));
  }
}
