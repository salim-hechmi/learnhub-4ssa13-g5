import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { OrderStatus, PaymentMethod, PaymentStatus } from '../models/marketplace.models';
import { studentColor, studentInitials, studentName } from './student-directory';

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'En attente', PAID: 'Payée', FAILED: 'Échouée', REFUNDED: 'Remboursée', SUCCESS: 'Succès'
};

@Component({
  selector: 'mk-status-badge',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span class="mk-badge" [class]="'mk-badge ' + status()">{{ label() }}</span>`
})
export class StatusBadgeComponent {
  status = input.required<OrderStatus | PaymentStatus>();
  label = computed(() => STATUS_LABELS[this.status()] ?? this.status());
}

@Component({
  selector: 'mk-method',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span [class]="'mk-method ' + method()"><i>{{ glyph() }}</i>{{ label() }}</span>`
})
export class PaymentMethodComponent {
  method = input.required<PaymentMethod>();
  glyph = computed(() => ({ STRIPE: 'S', PAYPAL: 'P', SIMULATOR: 'T' }[this.method()]));
  label = computed(() => ({ STRIPE: 'Stripe', PAYPAL: 'PayPal', SIMULATOR: 'Simulateur' }[this.method()]));
}

@Component({
  selector: 'mk-student',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span class="mk-chip" [title]="id()">
      <span class="mk-avatar" [style.background]="color()">{{ initials() }}</span>
      <span class="name">{{ name() }}</span>
    </span>`
})
export class StudentChipComponent {
  id = input.required<string>();
  name = computed(() => studentName(this.id()));
  initials = computed(() => studentInitials(this.id()));
  color = computed(() => studentColor(this.id()));
}

/** Petites icônes au trait (même style que le reste de l'interface LearnHub). */
const ICONS: Record<string, string> = {
  edit: '<path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/>',
  trash: '<path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="m19 6-1 14H6L5 6"/>',
  eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  refresh: '<path d="M21 12a9 9 0 1 1-2.64-6.36L21 8"/><path d="M21 3v5h-5"/>',
  print: '<path d="M6 9V3h12v6"/><rect x="6" y="14" width="12" height="7"/><path d="M6 18H4a1 1 0 0 1-1-1v-6a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v6a1 1 0 0 1-1 1h-2"/>'
};

@Component({
  selector: 'mk-icon',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<svg class="mk-ico" viewBox="0 0 24 24" aria-hidden="true" [innerHTML]="svg()"></svg>`,
  styles: [':host { display: inline-flex; }']
})
export class IconComponent {
  private readonly sanitizer = inject(DomSanitizer);
  name = input.required<keyof typeof ICONS | string>();
  svg = computed<SafeHtml>(() => this.sanitizer.bypassSecurityTrustHtml(ICONS[this.name()] ?? ''));
}
