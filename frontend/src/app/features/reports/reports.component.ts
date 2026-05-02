import { CommonModule, DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MonthlyReport } from '../../models/api.types';
import { SaleService } from '../../services/sale.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
})
export class ReportsComponent {
  year = new Date().getFullYear();
  month = new Date().getMonth() + 1;
  report: MonthlyReport | null = null;
  error = '';
  loading = false;

  constructor(private readonly saleService: SaleService) {}

  load(): void {
    this.loading = true;
    this.error = '';
    this.report = null;
    this.saleService.monthlyReport(this.year, this.month).subscribe({
      next: (r) => {
        this.report = r;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load report.';
        this.loading = false;
      },
    });
  }
}
