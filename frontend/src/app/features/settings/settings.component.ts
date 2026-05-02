import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ShopSettings } from '../../models/api.types';
import { SettingsService } from '../../services/settings.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
})
export class SettingsComponent implements OnInit {
  model: ShopSettings = { merchantVpa: '', merchantName: '', currency: 'INR' };
  message = '';
  error = '';
  loading = true;
  saving = false;

  constructor(private readonly settingsService: SettingsService) {}

  ngOnInit(): void {
    this.settingsService.get().subscribe({
      next: (s) => {
        this.model = { ...s };
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load settings.';
        this.loading = false;
      },
    });
  }

  save(): void {
    this.saving = true;
    this.message = '';
    this.error = '';
    this.settingsService.patch(this.model).subscribe({
      next: (s) => {
        this.model = { ...s };
        this.message = 'Saved.';
        this.saving = false;
      },
      error: (e) => {
        this.error = e.status === 401 ? 'Unauthorized (admin / admin).' : 'Save failed.';
        this.saving = false;
      },
    });
  }
}
