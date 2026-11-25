import { ChangeDetectionStrategy, Component, ViewEncapsulation, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ApiTraceService } from '../services/api-trace';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'mk-marketplace-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './marketplace-shell.component.html',
  styleUrls: ['../shared/marketplace-theme.css'],
  encapsulation: ViewEncapsulation.None, // le thème s'applique à toutes les pages enfants
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MarketplaceShellComponent {
  readonly trace = inject(ApiTraceService).last;
  readonly toast = inject(ToastService);

  readonly tabs = [
    { path: 'dashboard', label: 'Tableau de bord' },
    { path: 'orders', label: 'Commandes' },
    { path: 'payments', label: 'Transactions' },
    { path: 'invoices', label: 'Factures' }
  ];

  statusColor(status: number): string | null {
    if (status && status < 300) return null;
    return status && status < 500 ? 'var(--amber)' : 'var(--red)';
  }
}
