import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QRCodeModule } from 'angularx-qrcode';
import { Subject, interval, switchMap, takeUntil } from 'rxjs';
import {
  InvoiceResponse,
  PaymentQrResponse,
  PaymentStatusResponse,
} from '../../models/api.types';
import { GatewayPaymentService } from '../../services/gateway-payment.service';

@Component({
  selector: 'app-gateway-payment',
  standalone: true,
  imports: [CommonModule, FormsModule, QRCodeModule],
  templateUrl: './gateway-payment.component.html',
  styleUrl: './gateway-payment.component.css',
})
export class GatewayPaymentComponent implements OnDestroy {
  customerName = '';
  amount = 0;
  description = '';
  currency = 'INR';

  loading = false;
  error = '';

  invoice: InvoiceResponse | null = null;
  paymentQr: PaymentQrResponse | null = null;
  paymentStatus: PaymentStatusResponse | null = null;
  notification = '';

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly gatewayPaymentService: GatewayPaymentService) {}

  createInvoiceAndQr(): void {
    this.error = '';
    this.notification = '';
    this.paymentStatus = null;
    if (!this.customerName.trim() || this.amount <= 0) {
      this.error = 'Enter customer name and valid amount.';
      return;
    }
    this.loading = true;
    this.gatewayPaymentService
      .createInvoice({
        customerName: this.customerName.trim(),
        amount: this.amount,
        description: this.description.trim(),
        currency: this.currency.trim() || 'INR',
      })
      .subscribe({
        next: (invoice) => {
          this.invoice = invoice;
          this.gatewayPaymentService
            .createQr({ invoiceId: invoice.id, closeAfterSeconds: 900 })
            .subscribe({
              next: (qr) => {
                this.paymentQr = qr;
                this.loading = false;
                this.startStatusPolling(qr.paymentId);
              },
              error: (e) => {
                this.loading = false;
                this.error = e.error?.error ?? 'Could not create dynamic QR.';
              },
            });
        },
        error: (e) => {
          this.loading = false;
          this.error = e.error?.error ?? 'Could not create invoice.';
        },
      });
  }

  private startStatusPolling(paymentId: number): void {
    interval(3000)
      .pipe(
        takeUntil(this.destroy$),
        switchMap(() => this.gatewayPaymentService.getStatus(paymentId))
      )
      .subscribe({
        next: (status) => {
          this.paymentStatus = status;
          if (status.paymentStatus === 'PAID') {
            this.notification = 'Payment successful. Billing status updated.';
            this.stopPolling();
          } else if (
            status.paymentStatus === 'FAILED' ||
            status.paymentStatus === 'EXPIRED'
          ) {
            this.notification = `Payment ${status.paymentStatus.toLowerCase()}.`;
            this.stopPolling();
          }
        },
        error: () => {
          this.error = 'Could not fetch payment status.';
          this.stopPolling();
        },
      });
  }

  stopPolling(): void {
    this.destroy$.next();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

