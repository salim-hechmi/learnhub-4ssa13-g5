import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Invoice, Page } from '../models/marketplace.models';
import { readApiError } from '../services/http-error';
import { MarketplaceApiService } from '../services/marketplace-api.service';
import { ToastService } from '../services/toast.service';
import { studentName } from '../shared/student-directory';
import { IconComponent, PaymentMethodComponent, StudentChipComponent } from '../shared/ui.components';

@Component({
  selector: 'mk-invoices',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe, IconComponent, StudentChipComponent, PaymentMethodComponent],
  templateUrl: './invoices.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoicesComponent implements OnInit {
  private readonly api = inject(MarketplaceApiService);
  private readonly toast = inject(ToastService);

  readonly page = signal<Page<Invoice> | null>(null);
  readonly loading = signal(false);
  readonly viewing = signal<Invoice | null>(null);
  lookup = '';
  readonly studentName = studentName;

  ngOnInit(): void { this.load(0); }

  load(page: number): void {
    this.loading.set(true);
    this.api.invoices(page, 10).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: e => { this.fail(e); this.loading.set(false); }
    });
  }

  findByNumber(): void {
    const n = this.lookup.trim().toUpperCase();
    if (!n) return;
    this.api.invoiceByNumber(n).subscribe({ next: inv => this.viewing.set(inv), error: e => this.fail(e) });
  }

  print(): void { window.print(); }

  methodLabel(m: Invoice['paymentMethod']): string {
    return m ? { STRIPE: 'Carte bancaire (Stripe)', PAYPAL: 'PayPal', SIMULATOR: 'Simulateur' }[m] : '—';
  }

  private fail(e: unknown): void { const er = readApiError(e); this.toast.error(er.title, er.detail); }
}
