import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CheckoutLine, CheckoutResponse, MonthlyReport, PaymentQrResponse } from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class SaleService {
  private readonly base = `${environment.apiBaseUrl}/api/sales`;

  constructor(private readonly http: HttpClient) {}

  checkout(lines: CheckoutLine[]): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.base}/checkout`, { lines });
  }

  /** Razorpay dynamic QR (or UPI fallback) for a checkout sale — no separate gateway screen needed. */
  gatewayQr(saleId: number): Observable<PaymentQrResponse> {
    return this.http.post<PaymentQrResponse>(`${this.base}/${saleId}/gateway-qr`, {});
  }

  markPaid(saleId: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${saleId}/mark-paid`, {});
  }

  monthlyReport(year: number, month: number): Observable<MonthlyReport> {
    return this.http.get<MonthlyReport>(`${this.base}/monthly`, { params: { year: String(year), month: String(month) } });
  }
}
