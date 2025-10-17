import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AuthService } from '../../../core/services/auth.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  children?: MenuItem[];
}

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule
  ],
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.scss']
})
export class DashboardLayoutComponent {
  private authService = inject(AuthService);
  protected router = inject(Router);
  private breakpointObserver = inject(BreakpointObserver);

  currentUser = computed(() => this.authService.getCurrentUser());
  isSidenavOpen = signal(true);
  isMobile = signal(false);

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/dashboard'
    },
    {
      label: 'Clients',
      icon: 'people',
      route: '/clientes'
    },
    {
      label: 'Transactions',
      icon: 'receipt_long',
      route: '/transacoes'
    },
    {
      label: 'Categories',
      icon: 'category',
      route: '/categorias'
    }
  ];

  constructor() {
    // Monitor screen size changes
    this.breakpointObserver.observe([
      Breakpoints.Handset,
      Breakpoints.Tablet
    ]).subscribe(result => {
      this.isMobile.set(result.matches);
      this.isSidenavOpen.set(!result.matches);
    });
  }

  toggleSidenav(): void {
    this.isSidenavOpen.set(!this.isSidenavOpen());
  }

  logout(): void {
    this.authService.logout();
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);

    // Close sidenav on mobile after navigation
    if (this.isMobile()) {
      this.isSidenavOpen.set(false);
    }
  }

  getUserInitials(): string {
    const user = this.currentUser();
    if (!user || !user.name) return 'U';

    const names = user.name.split(' ');
    if (names.length >= 2) {
      return `${names[0][0]}${names[1][0]}`.toUpperCase();
    }
    return user.name.substring(0, 2).toUpperCase();
  }
}
