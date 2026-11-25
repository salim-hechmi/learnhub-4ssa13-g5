import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  CheckoutRequest, CheckoutResponse, CreateOrderRequest, Invoice, MarketplaceStats,
  Order, OrderFilters, OrderStatus, Page, Payment, PaymentMethod, PaymentStatus, UpdateOrderRequest
} from '../models/marketplace.models';

/**
 * Accès REST au marketplace-service, TOUJOURS via l'API Gateway (:8080).
 * Le jeton Keycloak est ajouté par l'intercepteur d'authentification commun du groupe.
 */
@Injectable({ providedIn: 'root' })
export class MarketplaceApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiGatewayUrl;

  // ------------------------------ Orders ------------------------------
  searchOrders(f: OrderFilters): Observable<Page<Order>> {
    return this.http.get<Page<Order>>(`${this.base}/api/orders`, { params: this.params(f) });
  }

  myOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.base}/api/orders/my`);
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.base}/api/orders/${id}`);
  }

  createOrder(body: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(`${this.base}/api/orders`, body);
  }

  updateOrder(id: number, body: UpdateOrderRequest): Observable<Order> {
    return this.http.put<Order>(`${this.base}/api/orders/${id}`, body);
  }

  updateStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.base}/api/orders/${id}/status`, { status });
  }

  refund(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.base}/api/orders/${id}/refund`, {});
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/orders/${id}`);
  }

  stats(): Observable<MarketplaceStats> {
    return this.http.get<MarketplaceStats>(`${this.base}/api/orders/stats`);
  }

  // ------------------------------ Payments ------------------------------
  checkout(body: CheckoutRequest): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.base}/api/payments/checkout`, body);
  }

  payments(status: PaymentStatus | '', method: PaymentMethod | '', page = 0, size = 10): Observable<Page<Payment>> {
    return this.http.get<Page<Payment>>(`${this.base}/api/payments`,
      { params: this.params({ status, method, page, size, sort: 'id,desc' }) });
  }

  paymentsOfOrder(orderId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.base}/api/payments/order/${orderId}`);
  }

  // ------------------------------ Invoices ------------------------------
  invoices(page = 0, size = 10): Observable<Page<Invoice>> {
    return this.http.get<Page<Invoice>>(`${this.base}/api/invoices`,
      { params: this.params({ page, size, sort: 'issuedAt,desc' }) });
  }

  invoice(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.base}/api/invoices/${id}`);
  }

  invoiceByNumber(number: string): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.base}/api/invoices/number/${encodeURIComponent(number)}`);
  }

  /** Ignore les filtres vides pour garder des URLs propres. */
  private params(obj: object): HttpParams {
    let p = new HttpParams();
    Object.entries(obj).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') p = p.set(k, String(v));
    });
    return p;
  }
}
