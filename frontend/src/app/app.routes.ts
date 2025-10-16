import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES)
  },
  {
    path: 'clientes',
    loadChildren: () => import('./features/clientes/clientes.routes').then(m => m.CLIENTES_ROUTES)
  },
  {
    path: 'transacoes',
    loadChildren: () => import('./features/transacoes/transacoes.routes').then(m => m.TRANSACOES_ROUTES)
  },
  {
    path: 'categorias',
    loadChildren: () => import('./features/categorias/categorias.routes').then(m => m.CATEGORIAS_ROUTES)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
