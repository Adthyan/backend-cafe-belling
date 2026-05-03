import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription, forkJoin, interval, switchMap } from 'rxjs';
import { QRCodeModule } from 'angularx-qrcode';
import { environment } from '../../../environments/environment';
import {
  CheckoutResponse,
  MenuItem,
  PaymentQrResponse,
  PaymentStatusResponse,
  ShopSettings,
} from '../../models/api.types';
import { CartService } from '../../services/cart.service';
import { GatewayPaymentService } from '../../services/gateway-payment.service';
import { MenuService } from '../../services/menu.service';
import { SaleService } from '../../services/sale.service';
import { SettingsService } from '../../services/settings.service';

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [CommonModule, FormsModule, QRCodeModule, DecimalPipe, DatePipe],
  templateUrl: './pos.component.html',
  styleUrl: './pos.component.css',
})
export class PosComponent implements OnInit, OnDestroy {
  menu: MenuItem[] = [];
  shopSettings: ShopSettings | null = null;
  error = '';
  loading = true;
  lastReceipt: CheckoutResponse | null = null;
  showPayPanel = false;
  checkoutBusy = false;
  toast = '';
  toastTimer: number | null = null;

  paymentQr: PaymentQrResponse | null = null;
  paymentStatus: PaymentStatusResponse | null = null;
  /** Kept after modal closes so re-print still shows the gateway ref */
  lastPaidGatewayId = '';
  gatewayQrError = '';
  qrLoading = false;

  private statusPoll?: Subscription;

  constructor(
    readonly cart: CartService,
    private readonly menuService: MenuService,
    private readonly saleService: SaleService,
    private readonly gatewayPaymentService: GatewayPaymentService,
    private readonly settingsService: SettingsService
  ) {}

  ngOnInit(): void {
    forkJoin({
      menu: this.menuService.listActive(),
      settings: this.settingsService.get(),
    }).subscribe({
      next: ({ menu, settings }) => {
        this.menu = menu;
        this.shopSettings = settings;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load menu. Start the Spring Boot API on port 8080.';
        this.loading = false;
      },
    });
  }

  ngOnDestroy(): void {
    this.stopPolling();
    if (this.toastTimer) {
      window.clearTimeout(this.toastTimer);
    }
  }

  imageSrc(item: MenuItem): string {
    return `${environment.apiBaseUrl}${item.imageUrl}`;
  }

  addToCart(item: MenuItem): void {
    this.error = '';
    this.cart.addItem(item);
  }

  qtyInCart(menuItemId: number): number {
    const line = this.cart.lines.find((l) => l.menuItem.id === menuItemId);
    return line?.quantity ?? 0;
  }

  clearCart(): void {
    this.cart.clear();
    this.error = '';
  }

  setQty(menuItemId: number, qty: number | string): void {
    const n = typeof qty === 'string' ? parseInt(qty, 10) : qty;
    if (Number.isNaN(n)) {
      return;
    }
    this.cart.setQuantity(menuItemId, n);
  }

  incrementQty(menuItemId: number, currentQty: number): void {
    this.cart.setQuantity(menuItemId, currentQty + 1);
  }

  decrementQty(menuItemId: number, currentQty: number): void {
    if (currentQty <= 0) {
      return;
    }
    this.cart.setQuantity(menuItemId, currentQty - 1);
  }

  subtotal(): number {
    return this.cart.subtotal();
  }

  checkoutAndPay(): void {
    this.error = '';
    if (this.cart.lines.length === 0) {
      this.error = 'Add items to the cart first.';
      return;
    }
    this.checkoutBusy = true;
    this.gatewayQrError = '';
    this.saleService.checkout(this.cart.toCheckoutLines()).subscribe({
      next: (r) => {
        this.lastReceipt = r;
        this.cart.clear();
        this.showPayPanel = true;
        this.checkoutBusy = false;
        this.paymentQr = null;
        this.paymentStatus = null;
        this.lastPaidGatewayId = '';
        this.requestGatewayQr(r.saleId);
      },
      error: (e) => {
        this.error = e.error?.error ?? 'Checkout failed.';
        this.checkoutBusy = false;
      },
    });
  }

  private requestGatewayQr(saleId: number): void {
    this.qrLoading = true;
    this.gatewayQrError = '';
    this.saleService.gatewayQr(saleId).subscribe({
      next: (qr) => {
        this.paymentQr = qr;
        this.qrLoading = false;
        this.startPolling(qr.paymentId);
      },
      error: (e) => {
        this.qrLoading = false;
        this.gatewayQrError = e.error?.error ?? 'Could not start payment gateway. Try Print bill and collect cash, or retry.';
      },
    });
  }

  retryGatewayQr(): void {
    if (!this.lastReceipt) {
      return;
    }
    this.stopPolling();
    this.requestGatewayQr(this.lastReceipt.saleId);
  }

  private startPolling(paymentId: number): void {
    this.stopPolling();
    this.statusPoll = interval(3000)
      .pipe(switchMap(() => this.gatewayPaymentService.getStatus(paymentId)))
      .subscribe({
        next: (status) => {
          this.paymentStatus = status;
          if (status.paymentStatus === 'PAID') {
            this.stopPolling();
            if (status.gatewayPaymentId) {
              this.lastPaidGatewayId = status.gatewayPaymentId;
            }
            this.showToast('Payment confirmed.');
            window.setTimeout(() => window.print(), 350);
            window.setTimeout(() => this.closePayPanel(), 800);
          } else if (status.paymentStatus === 'FAILED' || status.paymentStatus === 'EXPIRED') {
            this.stopPolling();
            this.showToast(`Payment ${status.paymentStatus.toLowerCase()}.`);
          }
        },
        error: () => {
          this.stopPolling();
          this.gatewayQrError = 'Lost connection while checking payment. You can retry or use Print bill.';
        },
      });
  }

  private stopPolling(): void {
    this.statusPoll?.unsubscribe();
    this.statusPoll = undefined;
  }

  closePayPanel(): void {
    this.stopPolling();
    this.showPayPanel = false;
    this.paymentQr = null;
    this.gatewayQrError = '';
    this.qrLoading = false;
  }

  showToast(message: string): void {
    this.toast = message;
    if (this.toastTimer) {
      window.clearTimeout(this.toastTimer);
    }
    this.toastTimer = window.setTimeout(() => {
      this.toast = '';
      this.toastTimer = null;
    }, 2500);
  }

  printBill(): void {
    if (!this.lastReceipt) {
      return;
    }
    window.print();
  }

  receiptShopName(): string {
    return this.shopSettings?.merchantName ?? 'Ramanujam & Janagam Family Cafe';
  }

  gatewayTxnRef(): string | undefined {
    return this.paymentStatus?.gatewayPaymentId || this.lastPaidGatewayId || undefined;
  }
}
