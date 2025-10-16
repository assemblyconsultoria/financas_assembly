import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-transacoes-list',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="page-container">
      <h1>Transactions</h1>
      <mat-card>
        <mat-card-content>
          <p>Transaction management coming soon...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .page-container {
      h1 {
        font-size: 28px;
        font-weight: 600;
        margin-bottom: 24px;
      }
    }
  `]
})
export class TransacoesListComponent {}
