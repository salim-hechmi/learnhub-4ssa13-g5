import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MarketplaceStats, Order } from '../models/marketplace.models';
import { MarketplaceApiService } from '../services/marketplace-api.service';
import { readApiError } from '../services/http-error';
import { ToastService } from '../services/toast.service';
import { IconComponent, StatusBadgeComponent, StudentChipComponent } from '../shared/ui.components';

@Component({
  selector: 'mk-dashboard',
  standalone: true,
  imports: [DecimalPipe, DatePipe, RouterLink, IconComponent, StatusBadgeComponent, StudentChipComponent],
  templateUrl: './dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(MarketplaceApiService);
  private readonly toast = inject(ToastService);

  readonly stats = signal<MarketplaceStats | null>(null);
  readonly recent = signal<Order[]>([]);
  readonly loading = signal(true);

  readonly distribution = computed(() => {
    const s = this.stats();
    if (!s) return [];
    const total = Math.max(s.totalOrders, 1);
    return [
      { label: 'Payées', value: s.paidOrders, color: '#34d399' },
      { label: 'En attente', value: s.pendingOrders, color: '#fbbf24' },
      { label: 'Échouées', value: s.failedOrders, color: '#f87171' },
      { label: 'Remboursées', value: s.refundedOrders, color: '#c084fc' }
    ].map(r => ({ ...r, pct: Math.round(r.value * 100 / total) }));
  });

  readonly maxSales = computed(() => Math.max(1, ...(this.stats()?.topCourses ?? []).map(c => c.salesCount)));

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    forkJoin({ stats: this.api.stats(), recent: this.api.searchOrders({ page: 0, size: 6, sort: 'createdAt,desc' }) })
      .subscribe({
        next: ({ stats, recent }) => { this.stats.set(stats); this.recent.set(recent.content); this.loading.set(false); },
        error: e => { const er = readApiError(e); this.toast.error(er.title, er.detail); this.loading.set(false); }
      });
  }
}
