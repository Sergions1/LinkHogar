import {Component, computed, effect, inject, signal} from '@angular/core';
import {AuthService} from '../../../services/auth/auth.service';
import {HomeService} from '../../../services/home/home-service';
import {HomeTaskService} from '../../../services/homeTask/home-task-service';
import {EventService} from '../../../services/event/event-service';
import {DatePipe, DecimalPipe, NgClass} from '@angular/common';
import {ExpenseService} from '../../../services/expense/expense-service';
import {DebtRepaymentDto} from '../../../Models/expense/DebtRepaymentDto';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home-dashboard',
  imports: [
    DatePipe,
    DecimalPipe,
    NgClass
  ],
  templateUrl: './home-dashboard.html',
  styleUrl: './home-dashboard.scss',
})
export class HomeDashboard {
  private authService = inject(AuthService);
  public homeService = inject(HomeService); // Público para usarlo en el HTML
  private taskService = inject(HomeTaskService);
  private eventService = inject(EventService);
  private expenseService = inject(ExpenseService);
  private router = inject(Router);

  currentUser = this.authService.currentUser;
  houseMembers = this.homeService.members;

  myNetBalance = computed(() => {
    const balances = this.expenseService.homeBalances();
    const user = this.currentUser();
    if (balances && user) {
      const myBalance = balances.balances.find(b => b.userId === user.id);
      return myBalance ? myBalance.netBalance : 0;
    }
    return 0;
  });

  myRelevantRepayments = computed(() => {
    const balances = this.expenseService.homeBalances();
    const user = this.currentUser();
    if (!balances || !user) return [];
    return balances.repayments.filter(r => r.debtorId === user.id || r.creditorId === user.id);
  });

  myNextRepayment = signal<DebtRepaymentDto | null>(null);

  isLoading = computed(() => this.expenseService.homeBalances() === null);

  //Filtramos las tareas que son para HOY
  todayTasks = computed(() => {
    const today = new Date().toDateString();
    const allTasks = this.taskService.tasks ? this.taskService.tasks() : [];

    return allTasks.filter(task => {
      // Si no está finalizada y tiene fecha de hoy
      if (task.status === 'Finalizada') return false;
      const taskDate = task.dueDate ? new Date(task.dueDate) : (task.startDate ? new Date(task.startDate) : null);
      return taskDate && taskDate.toDateString() === today;
    });
  });

  //Filtramos los eventos que son para HOY
  todayEvents = computed(() => {
    const today = new Date().toDateString();
    const allEvents = this.eventService.homeEvents ? this.eventService.homeEvents() : [];

    return allEvents.filter(event => {
      const eventStart = new Date(event.startDate);
      return eventStart.toDateString() === today;
    });
  });


  navigateAndOpenModal(path: string) {
    this.router.navigate(['/hogar', path], { queryParams: { modal: 'create' } });
  }
}
