// =====================================================================
//  LearnHub — Marketplace : modèles TypeScript alignés sur les DTOs Spring
// =====================================================================

export type OrderStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
export type PaymentMethod = 'STRIPE' | 'PAYPAL' | 'SIMULATOR';
export type PaymentStatus = 'SUCCESS' | 'FAILED';

export interface OrderItem {
  id: number;
  courseId: number;
  courseTitle: string;
  unitPrice: number;
}

export interface Payment {
  id: number;
  orderId: number;
  studentId: string;
  amount: number;
  currency: string;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionRef: string;
}

export interface Order {
  id: number;
  studentId: string;
  status: OrderStatus;
  totalAmount: number;
  currency: string;
  createdAt: string;
  paidAt: string | null;
  itemsCount: number;
  items: OrderItem[];
  payments: Payment[];
  invoiceId: number | null;
  invoiceNumber: string | null;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  issuedAt: string;
  orderId: number;
  studentId: string;
  items: OrderItem[];
  totalAmount: number;
  currency: string;
  paidAt: string;
  paymentMethod: PaymentMethod | null;
  transactionRef: string | null;
  /** PAID ou REFUNDED */
  orderStatus: OrderStatus;
}

export interface CheckoutResponse {
  paymentStatus: PaymentStatus;
  orderStatus: OrderStatus;
  message: string;
  payment: Payment;
  invoice: Invoice | null;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface TopCourse {
  courseId: number;
  courseTitle: string;
  salesCount: number;
  revenue: number;
}

export interface MarketplaceStats {
  totalOrders: number;
  pendingOrders: number;
  paidOrders: number;
  failedOrders: number;
  refundedOrders: number;
  totalRevenue: number;
  averageBasket: number;
  currency: string;
  totalPayments: number;
  successfulPayments: number;
  paymentSuccessRate: number;
  invoicesCount: number;
  topCourses: TopCourse[];
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}

// ----- Requêtes -----
export interface CreateOrderRequest {
  courseIds: number[];
  currency?: string;
  studentId?: string;
}

export interface UpdateOrderRequest {
  courseIds: number[];
  currency?: string;
}

export interface CheckoutRequest {
  orderId: number;
  method: PaymentMethod;
  cardNumber?: string;
  simulateFailure?: boolean;
}

export interface OrderFilters {
  status?: OrderStatus | '';
  studentId?: string;
  courseId?: number | null;
  q?: string;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
  sort?: string;
}
