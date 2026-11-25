# 🖥️ LearnHub — Backoffice Marketplace (Angular 17+)

Feature Angular **autonome** (composants standalone, signals, nouveau control flow `@if/@for`) à copier
dans le projet Angular commun du groupe. Le thème reprend la charte de l'interface commune
(fond slate sombre, cartes vitrées, accents bleu, pastilles de statut) sans dépendre de Tailwind.

```
src/app/features/marketplace/
├── marketplace.routes.ts           Routes lazy-loaded (dashboard, orders, payments, invoices)
├── models/marketplace.models.ts    Interfaces = DTOs Spring
├── services/
│   ├── marketplace-api.service.ts  Tous les appels REST (via Gateway :8080)
│   ├── catalog.service.ts          Lecture du catalogue course-service (sélecteur de cours)
│   ├── api-trace.ts                Intercepteur → « inspecteur HTTP » de l'en-tête
│   ├── dev-identity.interceptor.ts En-têtes X-User-* pour tester sans Gateway
│   ├── toast.service.ts / http-error.ts
├── shell/                          En-tête + onglets + toasts (charge le thème)
├── dashboard/                      KPI, top cours, taux de succès, dernières commandes
├── orders/                         CRUD complet : filtres, pagination, création, édition,
│                                   paiement sandbox, remboursement, suppression, tiroir détail
├── payments/                       Journal des transactions
├── invoices/                       Registre + facture imprimable (PDF via impression)
└── shared/                         Thème CSS, badges, puce étudiant
```

## Intégration (3 étapes)

**1. Copier** `src/app/features/marketplace` et la clé `apiGatewayUrl` / `devIdentity` dans `environment.ts`.

**2. Déclarer les intercepteurs** dans `app.config.ts` :
```ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { apiTraceInterceptor } from './features/marketplace/services/api-trace';
import { devIdentityInterceptor } from './features/marketplace/services/dev-identity.interceptor';

providers: [
  provideRouter(routes),
  provideHttpClient(withInterceptors([
    authInterceptor,            // intercepteur Keycloak commun du groupe (Bearer token)
    devIdentityInterceptor,     // inactif si environment.devIdentity = null
    apiTraceInterceptor
  ]))
]
```
`FormsModule` est importé directement par les composants.

**3. Ajouter la route** dans `app.routes.ts` :
```ts
{
  path: 'admin/marketplace',
  canActivate: [adminGuard],   // guard ADMIN du groupe
  loadChildren: () => import('./features/marketplace/marketplace.routes').then(m => m.MARKETPLACE_ROUTES)
}
```
→ http://localhost:3000/admin/marketplace

## Tester sans Gateway ni Keycloak
1. Lancer le backend : `mvn spring-boot:run -Dspring-boot.run.profiles=standalone` (CORS ouvert pour :3000 et :4200)
2. Dans `environment.ts` :
```ts
apiGatewayUrl: 'http://localhost:8085',
devIdentity: { userId: 'admin-0000-4000-8000-000000000099', roles: 'ADMIN', email: 'admin@learnhub.tn' }
```

## Aperçu sans Angular
`preview/marketplace-backoffice-preview.html` : la même interface en un seul fichier HTML, qui tourne
sur des données de démonstration (mêmes règles métier que le backend) ou contre l'API réelle
(bouton « API réelle » dans l'en-tête).
