import {Component, effect, inject} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {AuthService} from '../../../services/auth/auth.service';
import {HomeService} from '../../../services/home/home-service';
import {HomeTaskService} from '../../../services/homeTask/home-task-service';
import {EventService} from '../../../services/event/event-service';
import {ExpenseService} from '../../../services/expense/expense-service';
import {ChatService} from '../../../services/chat/chat-service';
import {WebSocketService} from '../../../services/chat/web-socket-service';

@Component({
  selector: 'app-home-layout',
    imports: [
        RouterLink,
        RouterLinkActive,
        RouterOutlet
    ],
  templateUrl: './home-layout.html',
  styleUrl: './home-layout.scss',
})
export class HomeLayout {
  isCollapsed = true;
  authService = inject(AuthService);
  homeService = inject(HomeService);
  taskService = inject(HomeTaskService);
  eventService = inject(EventService);
  expenseService = inject(ExpenseService);
  chatService = inject(ChatService);
  wsService = inject(WebSocketService);

  constructor() {
    effect(() => {
      const homeId = this.authService.currentUser()?.homeId;
      if (homeId) {
        // Disparamos la carga. El servicio se encarga de no repetirla si ya hay datos.
        this.homeService.loadMembers(homeId);

        this.taskService.getTasksByHome(homeId).subscribe();
        this.eventService.getHomeEvents(homeId).subscribe();

        this.expenseService.getHomeExpenses(homeId).subscribe({
          next: (data) => this.expenseService.homeExpenses.set(data)
        });

        this.expenseService.getHomeBalances(homeId).subscribe({
          next: (data) => this.expenseService.homeBalances.set(data)
        });

        this.chatService.getHomeChat(homeId).subscribe({
          next: (response) => {
            this.chatService.homeChatId.set(response.chatId);

            // 2. Conectamos el WebSocket a nivel global
            this.wsService.connect();
            this.wsService.subscribeToChat(response.chatId);

            // 3. Cargamos la primera página de mensajes de golpe
            this.chatService.getChatMessages(response.chatId, 0).subscribe({
              next: (msgs) => {
                const orderedMessages = [...msgs].reverse();
                this.chatService.homeChatMessages.set(orderedMessages);
                this.chatService.currentHomeMessagePage.set(1); // Dejamos preparada la paginación para el scroll

                if (msgs.length < 30) {
                  this.chatService.hasMoreHomeMessages.set(false);
                }
              }
            });
          }
        });
      }
    });
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}
