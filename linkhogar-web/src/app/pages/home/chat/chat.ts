import {Component, computed, effect, ElementRef, inject, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
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
  isLoadingChat = computed(() => !this.chatService.homeChatId());

  myUserId = computed(() => this.authService.currentUser()?.id);
  private wsSubscription?: Subscription;

  private tempMessageIdCounter = 0;

  constructor() {
    effect(() => {
      const messages = this.chatService.homeChatMessages();
      if (messages.length > 0) {
        setTimeout(() => this.scrollToBottom(), 100);
      }
    });
  }

  ngOnInit() {
    // Nos suscribimos a los mensajes entrantes del WebSocket
    this.wsSubscription = this.wsService.messageSubject.subscribe((nuevoMensaje) => {
      const currentChatId = this.chatService.homeChatId();

      if (currentChatId === nuevoMensaje.chatId) {
        // Actualizamos la CACHÉ directamente
        this.chatService.homeChatMessages.update(msgs => {
          const index = msgs.findIndex(m => m.isOptimistic && m.content === nuevoMensaje.content && m.senderId === nuevoMensaje.senderId);

          if (index !== -1) {
            // Si lo encontramos, lo reemplazamos por el real que viene del servidor
            const newMsgs = [...msgs];
            newMsgs[index] = nuevoMensaje;
            return newMsgs;
          } else {
            // Si no (ej. mensaje de otra persona), lo añadimos normal
            return [...msgs, nuevoMensaje];
          }
        });
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

        // Añadimos los mensajes antiguos al principio de la lista
        this.chatService.homeChatMessages.update(old => [...orderedMessages, ...old]);

        setTimeout(() => {
          if (container) {
            container.scrollTop = container.scrollHeight - previousScrollHeight;
          }
        }, 0);

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
    const userId = this.myUserId();
    const currentUser = this.authService.currentUser();

    if (!text || !chatId || !userId) {
      console.warn("No se puede enviar: Faltan datos", {text, chatId, userId});
      return;
    }

    const optimisticMsg = {
      id: `temp-${this.tempMessageIdCounter++}`, // Un ID falso temporal
      chatId: chatId,
      senderId: userId,
      senderName: currentUser ? `${currentUser.firstName} ${currentUser.lastName}` : 'Yo',
      content: text,
      createdAt: new Date().toISOString(),
      isOptimistic: true //ESTA ES LA CLAVE PARA DIFERENCIARLO EN EL HTML
    };

    this.chatService.homeChatMessages.update(msgs => [...msgs, optimisticMsg]);
    setTimeout(() => this.scrollToBottom(), 50);

    this.wsService.sendMessage(chatId, userId, text);
    this.newMessage.set('');
  }

  private scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }
}
