import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';

interface StatCard {
  title: string;
  value: string | number;
  icon: string;
  color: string;
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatGridListModule
  ],
  templateUrl: './dashboard-home.component.html',
  styleUrls: ['./dashboard-home.component.scss']
})
export class DashboardHomeComponent {
  stats = signal<StatCard[]>([
    {
      title: 'Total Clients',
      value: 0,
      icon: 'people',
      color: '#667eea',
      trend: {
        value: 0,
        isPositive: true
      }
    },
    {
      title: 'Total Transactions',
      value: 0,
      icon: 'receipt_long',
      color: '#764ba2',
      trend: {
        value: 0,
        isPositive: true
      }
    },
    {
      title: 'Total Revenue',
      value: 'R$ 0,00',
      icon: 'attach_money',
      color: '#4caf50',
      trend: {
        value: 0,
        isPositive: true
      }
    },
    {
      title: 'Total Expenses',
      value: 'R$ 0,00',
      icon: 'money_off',
      color: '#f44336',
      trend: {
        value: 0,
        isPositive: false
      }
    }
  ]);

  recentActivities = signal([
    {
      icon: 'info',
      title: 'Welcome to Financial Assembly',
      description: 'Start by adding your first client or transaction',
      time: 'Just now'
    }
  ]);

  quickActions = [
    {
      title: 'Add Client',
      icon: 'person_add',
      route: '/clientes/new',
      color: '#667eea'
    },
    {
      title: 'New Transaction',
      icon: 'add_circle',
      route: '/transacoes/new',
      color: '#764ba2'
    },
    {
      title: 'View Reports',
      icon: 'assessment',
      route: '/reports',
      color: '#4caf50'
    },
    {
      title: 'Manage Categories',
      icon: 'category',
      route: '/categorias',
      color: '#ff9800'
    }
  ];

  // Method to format currency
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }
}
