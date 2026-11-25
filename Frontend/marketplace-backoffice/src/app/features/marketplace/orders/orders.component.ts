import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import {
  CheckoutResponse, Order, OrderFilters, OrderStatus, Page, PaymentMethod
} from '../models/marketplace.models';
import { CatalogCourse, CatalogService } from '../services/catalog.service';
import { readApiError } from '../services/http-error';
import { MarketplaceApiService } from '../services/marketplace-api.service';
import { ToastService } from '../services/toast.service';
import { IconComponent, PaymentMethodComponent, StatusBadgeComponent, StudentChipComponent } from '../shared/ui.components';

type FormMode = 'create' | 'edit';
interface Confirm { kind: 'delete' | 'refund'; order: Order; }
interface TimelineStep { css: string; title: string; detail: string; when: string | null; }

@Component({
  selector: 'mk-orders',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe, IconComponent, StatusBadgeComponent, StudentChipComponent, PaymentMethodComponent],
  templateUrl: './orders.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrdersComponent implements OnInit {
  private readonly api = inject(MarketplaceApiService);
  private readonly toast = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);
  readonly catalog = inject(CatalogService);

  // ------------------------- Liste -------------------------
  readonly statusTabs: { value: OrderStatus | ''; label: string }[] = [
    { value: '', label: 'Toutes' }, { value: 'PENDING', label: 'En attente' }, { value: 'PAID', label: 'Payées' },
    { value: 'FAILED', label: 'Échouées' }, { value: 'REFUNDED', label: 'Remboursées' }
  ];
  readonly filters = signal<OrderFilters>({ status: '', q: '', from: '', to: '', courseId: null, page: 0, size: 10, sort: 'createdAt,desc' });
  readonly page = signal<Page<Order> | null>(null);
  readonly loading = signal(false);
  private readonly search$ = new Subject<string>();

  readonly pageNumbers = computed(() => {
    const p = this.page();
    if (!p || p.totalPages <= 1) return [];
    const start = Math.max(0, Math.min(p.page - 2, p.totalPages - 5));
    return Array.from({ length: Math.min(5, p.totalPages) }, (_, i) => start + i);
  });

  // ------------------------- Formulaire create / edit -------------------------
  readonly formMode = signal<FormMode | null>(null);
  readonly editing = signal<Order | null>(null);
  formStudentId = '';
  /** Les prix du catalogue sont en dinars : le backend refuse toute autre devise. */
  readonly currency = 'TND';
  readonly selected = signal<Map<number, CatalogCourse>>(new Map());
  manualCourseId: number | null = null;
  readonly saving = signal(false);
  readonly formTotal = computed(() => [...this.selected().values()].reduce((s, c) => s + (c.price || 0), 0));

  // ------------------------- Paiement -------------------------
  readonly paying = signal<Order | null>(null);
  payMethod: PaymentMethod = 'STRIPE';
  cardNumber = '4242424242424242';
  simulateFailure = false;
  readonly payResult = signal<CheckoutResponse | null>(null);
  readonly testCards = [
    { n: '4242424242424242', l: 'Acceptée' },
    { n: '4000000000000002', l: 'Refusée' },
    { n: '4000000000009995', l: 'Solde insuffisant' }
  ];
  private readonly methodNames: Record<PaymentMethod, string> = { STRIPE: 'Stripe', PAYPAL: 'PayPal', SIMULATOR: 'Simulateur' };
  private readonly statusNames: Record<OrderStatus, string> = { PENDING: 'en attente', PAID: 'payée', FAILED: 'échouée', REFUNDED: 'remboursée' };

  // ------------------------- Détail / confirmation -------------------------
  readonly detail = signal<Order | null>(null);
  readonly confirm = signal<Confirm | null>(null);

  readonly timeline = computed<TimelineStep[]>(() => {
    const o = this.detail();
    if (!o) return [];
    const steps: TimelineStep[] = [{
      css: 'info', title: 'Commande créée', when: o.createdAt, detail: `${o.itemsCount} cours`
    }];
    o.payments.forEach(p => steps.push({
      css: p.status === 'SUCCESS' ? 'ok' : 'ko',
      title: p.status === 'SUCCESS' ? 'Paiement accepté' : 'Paiement refusé',
      detail: `${this.methodNames[p.method]} · ${p.transactionRef}`, when: null
    }));
    if (o.invoiceNumber) steps.push({ css: 'ok', title: 'Facture émise', detail: o.invoiceNumber, when: o.paidAt });
    if (o.status === 'REFUNDED') steps.push({ css: 'refund', title: 'Commande remboursée', detail: '', when: null });
    return steps;
  });

  ngOnInit(): void {
    this.search$.pipe(debounceTime(350), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(q => this.patchFilters({ q, page: 0 }));
    this.load();
  }

  // ========================= Liste =========================
  load(): void {
    this.loading.set(true);
    this.api.searchOrders(this.filters()).subscribe({
      next: p => { this.page.set(p); this.loading.set(false); },
      error: e => { this.fail(e); this.loading.set(false); }
    });
  }

  patchFilters(patch: Partial<OrderFilters>): void {
    this.filters.update(f => ({ ...f, ...patch }));
    this.load();
  }

  onSearch(q: string): void { this.search$.next(q); }

  resetFilters(): void {
    this.filters.set({ status: '', q: '', from: '', to: '', courseId: null, page: 0, size: 10, sort: 'createdAt,desc' });
    this.load();
  }

  goTo(page: number): void { this.patchFilters({ page }); }

  isPayable(o: Order): boolean { return o.status === 'PENDING' || o.status === 'FAILED'; }

  // ========================= Create / Edit =========================
  openCreate(): void {
    this.catalog.load();
    this.editing.set(null);
    this.formStudentId = '';
    this.selected.set(new Map());
    this.formMode.set('create');
  }

  openEdit(o: Order): void {
    this.catalog.load();
    this.editing.set(o);
    this.selected.set(new Map(o.items.map(i => [i.courseId, { id: i.courseId, title: i.courseTitle, price: i.unitPrice }])));
    this.formMode.set('edit');
  }

  toggleCourse(c: CatalogCourse): void {
    this.selected.update(m => {
      const next = new Map(m);
      if (next.has(c.id)) next.delete(c.id); else next.set(c.id, c);
      return next;
    });
  }

  addManualCourse(): void {
    const id = Number(this.manualCourseId);
    if (!id || id < 1) return;
    const known = this.catalog.courses().find(c => c.id === id);
    this.selected.update(m => new Map(m).set(id, known ?? { id, title: `Cours #${id}`, price: 0 }));
    this.manualCourseId = null;
  }

  removeSelected(id: number): void {
    this.selected.update(m => { const n = new Map(m); n.delete(id); return n; });
  }

  submitForm(): void {
    const courseIds = [...this.selected().keys()];
    if (!courseIds.length) { this.toast.error('Sélection vide', 'Choisissez au moins un cours.'); return; }
    this.saving.set(true);
    const edit = this.editing();
    const call$ = edit
      ? this.api.updateOrder(edit.id, { courseIds })
      : this.api.createOrder({ courseIds, studentId: this.formStudentId.trim() || undefined });
    call$.subscribe({
      next: o => {
        this.saving.set(false);
        this.formMode.set(null);
        this.toast.success(edit ? `Commande #${o.id} mise à jour` : `Commande #${o.id} créée`,
          `${o.itemsCount} cours, ${o.totalAmount.toFixed(2)} ${o.currency}`);
        this.load();
        if (this.detail()?.id === o.id) this.detail.set(o);
      },
      error: e => { this.saving.set(false); this.fail(e); }
    });
  }

  // ========================= Paiement =========================
  openCheckout(o: Order): void {
    this.paying.set(o);
    this.payResult.set(null);
    this.payMethod = 'STRIPE';
    this.cardNumber = '4242424242424242';
    this.simulateFailure = false;
  }

  pay(): void {
    const o = this.paying();
    if (!o) return;
    this.saving.set(true);
    this.api.checkout({
      orderId: o.id, method: this.payMethod,
      cardNumber: this.payMethod === 'STRIPE' && this.cardNumber ? this.cardNumber.replace(/\D/g, '') : undefined,
      simulateFailure: this.simulateFailure || undefined
    }).subscribe({
      next: r => this.afterPayment(r),
      // 402 = paiement refusé : le corps contient quand même un CheckoutResponse
      error: e => {
        if (e?.status === 402 && e.error?.paymentStatus) { this.afterPayment(e.error as CheckoutResponse); return; }
        this.saving.set(false); this.fail(e);
      }
    });
  }

  private afterPayment(r: CheckoutResponse): void {
    this.saving.set(false);
    this.payResult.set(r);
    r.paymentStatus === 'SUCCESS'
      ? this.toast.success('Paiement accepté', `Facture ${r.invoice?.invoiceNumber}`)
      : this.toast.error('Paiement refusé', r.message);
    this.load();
    if (this.detail()) this.openDetail(this.detail()!);
  }

  // ========================= Détail =========================
  openDetail(o: Order): void {
    this.api.getOrder(o.id).subscribe({ next: full => this.detail.set(full), error: e => this.fail(e) });
  }

  changeStatus(o: Order, status: OrderStatus): void {
    this.api.updateStatus(o.id, status).subscribe({
      next: u => { this.detail.set(u); this.toast.success(`Commande #${u.id} : ${this.statusNames[u.status]}`); this.load(); },
      error: e => this.fail(e)
    });
  }

  // ========================= Suppression / remboursement =========================
  askDelete(o: Order): void { this.confirm.set({ kind: 'delete', order: o }); }
  askRefund(o: Order): void { this.confirm.set({ kind: 'refund', order: o }); }

  doConfirm(): void {
    const c = this.confirm();
    if (!c) return;
    this.saving.set(true);
    const done = (msg: string) => {
      this.saving.set(false); this.confirm.set(null); this.toast.success(msg); this.load();
    };
    if (c.kind === 'delete') {
      this.api.deleteOrder(c.order.id).subscribe({
        next: () => { done(`Commande #${c.order.id} supprimée`); if (this.detail()?.id === c.order.id) this.detail.set(null); },
        error: e => { this.saving.set(false); this.fail(e); }
      });
    } else {
      this.api.refund(c.order.id).subscribe({
        next: u => { done(`Commande #${u.id} remboursée`); if (this.detail()?.id === u.id) this.detail.set(u); },
        error: e => { this.saving.set(false); this.fail(e); }
      });
    }
  }

  private fail(e: unknown): void {
    const er = readApiError(e);
    this.toast.error(er.title, er.detail);
  }
}
