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

  isLoading = signal<boolean>(true);
  myUserId: string | undefined;
  homeId: string | undefined;

  showAddModal = signal<boolean>(false);
  homeMembers = this.homeService.members;

  balancesData = signal<HomeBalancesResponse | null>(null);

  // Para el acordeón de splits
  expandedExpenseId = signal<string | null>(null);

  myBalance = computed(() => {
    const data = this.balancesData();
    if (!data || !this.myUserId) return 0;
    const myData = data.balances.find(b => b.userId === this.myUserId);
    return myData ? myData.netBalance : 0;
  });

  myRelevantRepayments = computed(() => {
    const data = this.balancesData();
    if (!data || !this.myUserId) return [];
    return data.repayments.filter(r => r.debtorId === this.myUserId || r.creditorId === this.myUserId);
  });

  constructor() {
    this.route.queryParams.subscribe(params => {
      if (params['modal'] === 'create') {
        this.showAddModal.set(true);
      }
    });

    effect(() => {
      const user = this.authService.currentUser();
      if (user?.id && user?.homeId) {
        this.myUserId = user.id;
        this.homeId = user.homeId;
        this.loadExpensesAndBalances(user.homeId);
      }
    });
  }

  loadExpensesAndBalances(homeId: string) {
    this.loadExpenses(homeId);
    this.loadBalances(homeId);
  }

  loadExpenses(homeId: string) {
    this.isLoading.set(true);
    this.expenseService.getHomeExpenses(homeId).subscribe({
      next: (data) => {
        this.expenseService.homeExpenses.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando gastos', err);
        this.isLoading.set(false);
      }
    });
  }

  loadBalances(homeId: string) {
    this.expenseService.getHomeBalances(homeId).subscribe({
      next: (data) => this.balancesData.set(data),
      error: (err) => console.error('Error cargando balances', err)
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
          },
          error: (err) => console.error('Error eliminando el gasto', err)
        });
      }
    });
  }

  onExpenseCreated() {
    this.showAddModal.set(false); // Cerramos el modal
    if (this.homeId) {
      this.loadExpensesAndBalances(this.homeId);
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
    this.expenseService.markSplitAsPaid(splitId).subscribe({
      next: () => {
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
        if (this.homeId) this.loadBalances(this.homeId);
      },
      error: (err) => console.error('Error marcando como pagado', err)
    });
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
