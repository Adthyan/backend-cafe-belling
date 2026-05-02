import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CreateInvoiceRequest,
  CreateQrPaymentRequest,
  InvoiceResponse,
  PaymentMonitorRow,
  PaymentQrResponse,
  PaymentStatusResponse,
} from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class GatewayPaymentService {
  private readonly invoiceBase = `${environment.apiBaseUrl}/api/invoices`;
  private readonly paymentBase = `${environment.apiBaseUrl}/api/payments`;

  constructor(private readonly http: HttpClient) {}

  createInvoice(body: CreateInvoiceRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(this.invoiceBase, body);
  }

  createQr(body: CreateQrPaymentRequest): Observable<PaymentQrResponse> {
    return this.http.post<PaymentQrResponse>(`${this.paymentBase}/qr`, body);
  }

  getStatus(paymentId: number): Observable<PaymentStatusResponse> {
    return this.http.get<PaymentStatusResponse>(`${this.paymentBase}/${paymentId}/status`);
  }

  monitorRows(): Observable<PaymentMonitorRow[]> {
    return this.http.get<PaymentMonitorRow[]>(`${this.paymentBase}/monitor`);
  }
}

