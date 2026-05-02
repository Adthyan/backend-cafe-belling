import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/pos/pos.component').then((m) => m.PosComponent),
  },
  {
    path: 'admin/menu',
    loadComponent: () => import('./features/admin-menu/admin-menu.component').then((m) => m.AdminMenuComponent),
  },
  {
    path: 'reports',
    loadComponent: () => import('./features/reports/reports.component').then((m) => m.ReportsComponent),
  },
  {
    path: 'settings',
    loadComponent: () => import('./features/settings/settings.component').then((m) => m.SettingsComponent),
  },
  {
    path: 'payments-monitor',
    loadComponent: () =>
      import('./features/payments-monitor/payments-monitor.component').then(
        (m) => m.PaymentsMonitorComponent
      ),
  },
  { path: '**', redirectTo: '' },
];
