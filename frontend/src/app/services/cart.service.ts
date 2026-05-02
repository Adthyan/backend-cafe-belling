import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CheckoutLine, MenuItem } from '../models/api.types';

export interface CartLine {
  menuItem: MenuItem;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly linesSubject = new BehaviorSubject<CartLine[]>([]);
  readonly lines$ = this.linesSubject.asObservable();

  get lines(): CartLine[] {
    return this.linesSubject.value;
  }

  addItem(menuItem: MenuItem): void {
    const lines = [...this.linesSubject.value];
    const idx = lines.findIndex((l) => l.menuItem.id === menuItem.id);
    if (idx >= 0) {
      lines[idx] = { ...lines[idx], quantity: lines[idx].quantity + 1 };
    } else {
      lines.push({ menuItem, quantity: 1 });
    }
    this.linesSubject.next(lines);
  }

  setQuantity(menuItemId: number, quantity: number): void {
    if (quantity < 1) {
      this.removeLine(menuItemId);
      return;
    }
    const lines = this.linesSubject.value.map((l) =>
      l.menuItem.id === menuItemId ? { ...l, quantity } : l
    );
    this.linesSubject.next(lines);
  }

  removeLine(menuItemId: number): void {
    this.linesSubject.next(this.linesSubject.value.filter((l) => l.menuItem.id !== menuItemId));
  }

  clear(): void {
    this.linesSubject.next([]);
  }

  subtotal(): number {
    return this.linesSubject.value.reduce((sum, l) => sum + l.menuItem.price * l.quantity, 0);
  }

  toCheckoutLines(): CheckoutLine[] {
    return this.linesSubject.value.map((l) => ({
      menuItemId: l.menuItem.id,
      quantity: l.quantity,
    }));
  }
}
