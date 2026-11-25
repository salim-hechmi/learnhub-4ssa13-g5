import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Page, Payment, PaymentMethod, PaymentStatus } from '../models/marketplace.models';
import { readApiError } from '../services/http-error';
import { MarketplaceApiService } from '../services/marketplace-api.service';
import { ToastService } from '../services/toast.service';
import { IconComponent, PaymentMethodComponent, StatusBadgeComponent, StudentChipComponent } from '../shared/ui.components';

@Component({
  selector: 'mk-payments',
  standalone: true,
  imports: [FormsModule, DecimalPipe, IconComponent, StatusBadgeComponent, PaymentMethodComponent, StudentChipComponent],
  templateUrl: './payments.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsComponent implements OnInit {
  private readonly api = inject(MarketplaceApiService);
  private readonly toast = inject(ToastService);

  status: PaymentStatus | '' = '';
  method: PaymentMethod | '' = '';
  readonly page = signal<Page<Payment> | null>(null);
  readonly loading = signal(false);
  private current = 0;

  /** Indicateurs calculés sur la page affichée. */
  readonly pageSummary = computed(() => {
    const rows = this.page()?.content ?? [];
    const ok = rows.filter(p => p.status === 'SUCCESS');
    return { ok: ok.length, ko: rows.length - ok.length, amount: ok.reduce((s, p) => s + p.amount, 0) };
  });

  ngOnInit(): void { this.load(0); }

  load(page: number): void {
    this.current = page;
    this.loading.set(true);
    this.api.payments(this.status, this.method, page, 10).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: e => { const er = readApiError(e); this.toast.error(er.title, er.detail); this.loading.set(false); }
    });
  }

  copy(ref: string): void {
    navigator.clipboard?.writeText(ref);
    this.toast.info('Référence copiée', ref);
  }

  refresh(): void { this.load(this.current); }
}
