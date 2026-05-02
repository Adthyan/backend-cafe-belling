import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, interval, startWith, switchMap, takeUntil } from 'rxjs';
import { PaymentMonitorRow } from '../../models/api.types';
import { GatewayPaymentService } from '../../services/gateway-payment.service';

@Component({
  selector: 'app-payments-monitor',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './payments-monitor.component.html',
  styleUrl: './payments-monitor.component.css',
})
export class PaymentsMonitorComponent implements OnInit, OnDestroy {
  rows: PaymentMonitorRow[] = [];
  loading = true;
  error = '';
  private readonly destroy$ = new Subject<void>();

  constructor(private readonly gatewayPaymentService: GatewayPaymentService) {}

  ngOnInit(): void {
    interval(5000)
      .pipe(
        startWith(0),
        takeUntil(this.destroy$),
        switchMap(() => this.gatewayPaymentService.monitorRows())
      )
      .subscribe({
        next: (rows) => {
          this.rows = rows;
          this.loading = false;
          this.error = '';
        },
        error: () => {
          this.loading = false;
          this.error = 'Could not load payments monitor.';
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  rowClass(status: PaymentMonitorRow['paymentStatus']): string {
    if (status === 'PAID') return 'paid';
    if (status === 'FAILED' || status === 'EXPIRED') return 'failed';
    return 'pending';
  }
}

