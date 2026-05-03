import {Component, effect, ElementRef, inject, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {ChatService} from '../../../services/chat/chat-service';
import {AuthService} from '../../../services/auth/auth.service';
import {WebSocketService} from '../../../services/chat/web-socket-service';
import {Subscription} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-chat',
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './chat.html',
  styleUrl: './chat.scss',
})
export class Chat implements OnInit, OnDestroy {
  public chatService = inject(ChatService);
  private authService = inject(AuthService);
  private wsService = inject(WebSocketService);

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  newMessage = signal<string>('');
  isLoadingMore = signal<boolean>(false);
  isLoadingChat = signal<boolean>(true);

  myUserId = this.authService.currentUser()?.id;
  private wsSubscription?: Subscription;

  constructor() {
    effect(() => {
      const user = this.authService.currentUser();
      if (user?.homeId && user?.id) {
        this.myUserId = user.id;
        this.wsService.connect();
        this.initializeHomeChat(user.homeId);
      }
    });
  }

  ngOnInit() {
    // Nos suscribimos a los mensajes entrantes del WebSocket
    this.wsSubscription = this.wsService.messageSubject.subscribe((nuevoMensaje) => {
      const currentChatId = this.chatService.homeChatId();

      if (currentChatId === nuevoMensaje.chatId) {
        // Actualizamos la CACHÉ directamente
        this.chatService.homeChatMessages.update(msgs => [...msgs, nuevoMensaje]);
        setTimeout(() => this.scrollToBottom(), 100);
      }
    });
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    // No nos desuscribimos del WebSocket del ChatService para que siga escuchando en background!
  }

  initializeHomeChat(homeId: string) {
    // Si ya tenemos el chat cargado en memoria, no hacemos la petición inicial
    if (this.chatService.homeChatId()) {
      this.isLoadingChat.set(false);
      setTimeout(() => this.scrollToBottom(), 100);
      return;
    }

    this.isLoadingChat.set(true);

    this.chatService.getHomeChat(homeId).subscribe({
      next: (response) => {
        this.chatService.homeChatId.set(response.chatId);
        this.wsService.subscribeToChat(response.chatId);
        this.loadMessages();
        this.isLoadingChat.set(false);
      },
      error: (err) => {
        console.error('Error inicializando el chat del hogar', err);
        this.isLoadingChat.set(false);
      }
    });
  }

  loadMessages() {
    const chatId = this.chatService.homeChatId();
    if (!chatId || !this.chatService.hasMoreHomeMessages() || this.isLoadingMore()) return;

    this.isLoadingMore.set(true);
    const currentPage = this.chatService.currentHomeMessagePage();
    const container = this.myScrollContainer?.nativeElement;
    const previousScrollHeight = container ? container.scrollHeight : 0;

    this.chatService.getChatMessages(chatId, currentPage).subscribe({
      next: (newMessages) => {
        const orderedMessages = [...newMessages].reverse();

        if (newMessages.length < 30) {
          this.chatService.hasMoreHomeMessages.set(false);
        }

        if (currentPage === 0) {
          this.chatService.homeChatMessages.set(orderedMessages);
          setTimeout(() => this.scrollToBottom(), 100);
        } else {
          this.chatService.homeChatMessages.update(old => [...orderedMessages, ...old]);
          setTimeout(() => {
            if (container) {
              container.scrollTop = container.scrollHeight - previousScrollHeight;
            }
          }, 0);
        }

        this.chatService.currentHomeMessagePage.update(p => p + 1);
        this.isLoadingMore.set(false);
      },
      error: (err) => {
        console.error('Error cargando historial', err);
        this.isLoadingMore.set(false);
      }
    });
  }

  sendMessage() {
    const text = this.newMessage().trim();
    const chatId = this.chatService.homeChatId();

    if (!text || !chatId || !this.myUserId) return;

    this.wsService.sendMessage(chatId, this.myUserId, text);
    this.newMessage.set('');
  }

  private scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }
}
