import { CommonModule, DecimalPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { MenuItem, MenuItemWrite } from '../../models/api.types';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-admin-menu',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe],
  templateUrl: './admin-menu.component.html',
  styleUrl: './admin-menu.component.css',
})
export class AdminMenuComponent implements OnInit {
  items: MenuItem[] = [];
  error = '';
  successMessage = '';
  loading = true;
  saving = false;

  editing: MenuItem | null = null;
  form: MenuItemWrite = this.emptyForm();

  constructor(private readonly menuService: MenuService) {}

  ngOnInit(): void {
    this.reload();
  }

  imageSrc(url: string): string {
    if (url.startsWith('http')) return url;
    return `${environment.apiBaseUrl}${url}`;
  }

  emptyForm(): MenuItemWrite {
    return {
      name: '',
      description: '',
      price: 0,
      imageUrl: '/menu/Idlly.jpg',
      active: true,
      sortOrder: 0,
    };
  }

  showSuccess(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }

  reload(): void {
    this.loading = true;
    this.error = '';
    this.menuService.listAll().subscribe({
      next: (items) => {
        this.items = items;
        this.loading = false;
      },
      error: (e) => {
        this.error =
          e.status === 401
            ? 'Unauthorized. Use admin / admin in the API (HTTP Basic) — check browser network tab.'
            : 'Could not load menu items. Check if backend is running.';
        this.loading = false;
      },
    });
  }

  newItem(): void {
    this.editing = null;
    this.form = this.emptyForm();
  }

  edit(item: MenuItem): void {
    this.editing = item;
    this.form = {
      name: item.name,
      description: item.description ?? '',
      price: item.price,
      imageUrl: item.imageUrl,
      active: item.active,
      sortOrder: item.sortOrder,
    };
  }

  save(): void {
    this.saving = true;
    this.error = '';
    this.successMessage = '';
    const req =
      this.editing == null
        ? this.menuService.create(this.form)
        : this.menuService.update(this.editing.id, this.form);
    req.subscribe({
      next: () => {
        this.saving = false;
        this.showSuccess(this.editing ? 'Item updated successfully!' : 'Item created successfully!');
        this.newItem();
        this.reload();
      },
      error: (e) => {
        this.error = e.error?.error ?? 'Save failed (need admin credentials).';
        this.saving = false;
      },
    });
  }

  delete(item: MenuItem): void {
    if (!confirm(`Delete ${item.name}? This action cannot be undone.`)) {
      return;
    }
    this.error = '';
    this.successMessage = '';
    this.menuService.delete(item.id).subscribe({
      next: () => {
        this.showSuccess('Item deleted successfully!');
        this.reload();
      },
      error: (e) => {
        this.error = e.error?.error ?? 'Delete failed.';
      },
    });
  }
}
