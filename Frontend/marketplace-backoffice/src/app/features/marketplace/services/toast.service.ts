import { Injectable, signal } from '@angular/core';

export interface Toast { id: number; kind: 'success' | 'error' | 'info'; title: string; detail?: string; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private seq = 0;

  success(title: string, detail?: string) { this.push('success', title, detail); }
  error(title: string, detail?: string) { this.push('error', title, detail); }
  info(title: string, detail?: string) { this.push('info', title, detail); }

  dismiss(id: number) { this.toasts.update(list => list.filter(t => t.id !== id)); }

  private push(kind: Toast['kind'], title: string, detail?: string) {
    const toast: Toast = { id: ++this.seq, kind, title, detail };
    this.toasts.update(list => [...list, toast]);
    setTimeout(() => this.dismiss(toast.id), 4500);
  }
}
