export interface MenuItem {
  id: number;
  name: string;
  description: string | null;
  price: number;
  imageUrl: string;
  active: boolean;
  sortOrder: number;
}

export interface MenuItemWrite {
  name: string;
  description?: string | null;
  price: number;
  imageUrl: string;
  active: boolean;
  sortOrder: number;
}

export interface CheckoutLine {
  menuItemId: number;
  quantity: number;
}

export interface CheckoutLineResponse {
  menuItemId: number;
  name: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface CheckoutResponse {
  saleId: number;
  soldAt: string;
  lines: CheckoutLineResponse[];
  subtotal: number;
  tax: number;
  total: number;
  upiUri: string;
}

export interface ShopSettings {
  merchantVpa: string;
  merchantName: string;
  currency: string;
}

export interface DailySale {
  date: string;
  saleCount: number;
  revenue: number;
}

export interface MonthlyReport {
  year: number;
  month: number;
  totalSaleCount: number;
  totalRevenue: number;
  dailyBreakdown: DailySale[];
}

export interface CreateInvoiceRequest {
  customerName: string;
  amount: number;
  description?: string;
  currency?: string;
}

export interface InvoiceResponse {
  id: number;
  invoiceNumber: string;
  customerName: string;
  amount: number;
  currency: string;
  description?: string;
  status: 'UNPAID' | 'PAID' | 'FAILED';
  createdAt: string;
}

export interface CreateQrPaymentRequest {
  invoiceId: number;
  closeAfterSeconds?: number;
}

export interface PaymentQrResponse {
  paymentId: number;
  invoiceId: number;
  invoiceNumber: string;
  gatewayQrId: string;
  qrImageUrl?: string;
  qrContent?: string;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'EXPIRED';
  amount: number;
  currency: string;
  createdAt: string;
}

export interface PaymentStatusResponse {
  paymentId: number;
  invoiceId: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'EXPIRED';
  invoiceStatus: 'UNPAID' | 'PAID' | 'FAILED';
  gatewayPaymentId?: string;
  failureReason?: string;
}

export interface PaymentMonitorRow {
  paymentId: number;
  invoiceNumber: string;
  customerName: string;
  amount: number;
  currency: string;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'EXPIRED';
  invoiceStatus: 'UNPAID' | 'PAID' | 'FAILED';
  gatewayPaymentId?: string;
  failureReason?: string;
  createdAt: string;
  updatedAt: string;
}
