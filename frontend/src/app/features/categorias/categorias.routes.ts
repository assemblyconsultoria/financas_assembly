import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';

export const CATEGORIAS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('../dashboard/layout/dashboard-layout.component').then(m => m.DashboardLayoutComponent),
        children: [
          {
            path: '',
            loadComponent: () => import('./list/categorias-list.component').then(m => m.CategoriasListComponent)
          }
        ]
      }
    ]
  }
];
