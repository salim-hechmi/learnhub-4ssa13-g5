import { Routes } from '@angular/router';

/**
 * À brancher dans app.routes.ts du projet commun :
 *   { path: 'admin/marketplace', canActivate: [adminGuard], loadChildren: () =>
 *       import('./features/marketplace/marketplace.routes').then(m => m.MARKETPLACE_ROUTES) }
 */
export const MARKETPLACE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./shell/marketplace-shell.component').then(m => m.MarketplaceShellComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', title: 'Marketplace — Tableau de bord',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'orders', title: 'Marketplace — Commandes',
        loadComponent: () => import('./orders/orders.component').then(m => m.OrdersComponent) },
      { path: 'payments', title: 'Marketplace — Transactions',
        loadComponent: () => import('./payments/payments.component').then(m => m.PaymentsComponent) },
      { path: 'invoices', title: 'Marketplace — Factures',
        loadComponent: () => import('./invoices/invoices.component').then(m => m.InvoicesComponent) }
    ]
  }
];
