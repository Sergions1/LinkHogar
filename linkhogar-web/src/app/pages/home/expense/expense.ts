import {Component, computed, effect, inject, OnDestroy, signal} from '@angular/core';
import {ExpenseCategory} from '../../../Models/expense/ExpenseCategory';
import {ExpenseService} from '../../../services/expense/expense-service';
import {AuthService} from '../../../services/auth/auth.service';
import Swal from 'sweetalert2';
import {CurrencyPipe, DatePipe, NgClass} from '@angular/common';
import {ExpenseCategoryPipe} from '../../../pipes/ExpenseCategoryPipe';
import {AddExpenseModal} from './add-expense-modal/add-expense-modal';
import {HomeService} from '../../../services/home/home-service';
import {HomeBalancesResponse} from '../../../Models/expense/HomeBalancesResponse';
import {ExpenseSplitResponse} from '../../../Models/expense/ExpenseSplitResponse';
import {WebSocketService} from '../../../services/chat/web-socket-service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-expense',
  imports: [
    NgClass,
    DatePipe,
    CurrencyPipe,
    ExpenseCategoryPipe,
    AddExpenseModal
  ],
  templateUrl: './expense.html',
  styleUrl: './expense.scss',
})
export class Expense{
  public expenseService = inject(ExpenseService);
  private authService = inject(AuthService);
  private homeService = inject(HomeService);
  private route = inject(ActivatedRoute);


  myUserId = computed(() => this.authService.currentUser()?.id);
  homeId = computed(() => this.authService.currentUser()?.homeId);

  isLoading = computed(() => this.expenseService.homeBalances() === null);

  showAddModal = signal<boolean>(false);
  homeMembers = this.homeService.members;

  balancesData = signal<HomeBalancesResponse | null>(null);

  // Para el acordeón de splits
  expandedExpenseId = signal<string | null>(null);

  myBalance = computed(() => {
    const data = this.expenseService.homeBalances();
    const userId = this.myUserId();
    if (!data || !userId) return 0;

    const myData = data.balances.find(b => b.userId === userId);
    return myData ? myData.netBalance : 0;
  });

  myRelevantRepayments = computed(() => {
    const data = this.expenseService.homeBalances();
    const userId = this.myUserId();
    if (!data || !userId) return [];

    return data.repayments.filter(r => r.debtorId === userId || r.creditorId === userId);
  });

  constructor() {
    this.route.queryParams.subscribe(params => {
      if (params['modal'] === 'create') {
        this.showAddModal.set(true);
      }
    });
  }

  deleteExpense(expenseId: string, event: Event) {
    event.stopPropagation();
    Swal.fire({
      icon: 'warning',
      title: 'Atención',
      text: '¿Está seguro que desea eliminar el gasto?',
      confirmButtonText: "Eliminar",
      confirmButtonColor: "red",
      cancelButtonText: "Cancelar",
      cancelButtonColor: "var(--color-acento)"
    }).then((result) => {
      if (result.value) {
        this.expenseService.deleteExpense(expenseId).subscribe({
          next: () => {
            // Actualizamos la caché local eliminando el gasto
            this.expenseService.homeExpenses.update(expenses =>
              expenses.filter(e => e.id !== expenseId)
            );
            this.refreshBalances();
          },
          error: (err) => console.error('Error eliminando el gasto', err)
        });
      }
    });
  }

  onExpenseCreated() {
    this.showAddModal.set(false); // Cerramos el modal
    const currentHomeId = this.homeId();
    if (currentHomeId) {
      this.expenseService.getHomeExpenses(currentHomeId).subscribe({
        next: (data) => this.expenseService.homeExpenses.set(data)
      });
      this.refreshBalances();
    }
  }

  toggleExpenseSplits(expenseId: string) {
    if (this.expandedExpenseId() === expenseId) {
      this.expandedExpenseId.set(null);
    } else {
      this.expandedExpenseId.set(expenseId);
    }
  }

  markAsPaid(splitId: string, expenseId: string, event: Event) {
    event.stopPropagation();

    this.expenseService.homeExpenses.update(expenses =>
      expenses.map(exp => {
        if (exp.id === expenseId) {
          return {
            ...exp,
            splits: exp.splits.map(s => s.id === splitId ? { ...s, paid: true } : s)
          };
        }
        return exp;
      })
    );

    this.expenseService.markSplitAsPaid(splitId).subscribe({
      next: () => {
        this.refreshBalances();
      },
      error: (err) => {
        console.error('Error marcando como pagado', err);

        //ROLLBACK: Si falla, revertimos el split a 'Pendiente' (paid: false)
        this.expenseService.homeExpenses.update(expenses =>
          expenses.map(exp => {
            if (exp.id === expenseId) {
              return {
                ...exp,
                splits: exp.splits.map(s => s.id === splitId ? { ...s, paid: false } : s)
              };
            }
            return exp;
          })
        );

        // Y le avisamos al usuario para que sepa qué ha pasado
        Swal.fire({
          icon: 'error',
          title: 'Error de conexión',
          text: 'No se ha podido registrar el pago. Inténtalo de nuevo.',
          confirmButtonColor: 'var(--color-acento)',
          confirmButtonText: 'Entendido'
        });
      }
    });
  }

  private refreshBalances() {
    const currentHomeId = this.homeId();
    if (currentHomeId) {
      this.expenseService.getHomeBalances(currentHomeId).subscribe({
        next: (data) => this.expenseService.homeBalances.set(data), //Actualizamos la memoria global
        error: (err) => console.error('Error actualizando balances', err)
      });
    }
  }

  getCategoryIcon(category: ExpenseCategory): string {
    const icons: Record<ExpenseCategory, string> = {
      [ExpenseCategory.ALQUILER]: 'bi-house-door',
      [ExpenseCategory.SUPERMERCADO]: 'bi-cart3',
      [ExpenseCategory.SUMINISTROS]: 'bi-lightning',
      [ExpenseCategory.INTERNET]: 'bi-wifi',
      [ExpenseCategory.LIMPIEZA]: 'bi-stars',
      [ExpenseCategory.OTROS]: 'bi-receipt'
    };
    return icons[category] || 'bi-receipt';
  }


}
