import {Component, inject, OnInit, signal, ElementRef, ViewChild, OnDestroy, computed} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import {ChatService} from '../../../services/chat/chat-service';
import {WebSocketService} from '../../../services/chat/web-socket-service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './messages.html',
  styleUrls: ['./messages.scss']
})
export class Messages implements OnInit, OnDestroy {
  private chatService = inject(ChatService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private wsService = inject(WebSocketService);

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  chats = signal<any[]>([]);
  selectedChatId = signal<string | null>(null);
  currentMessages = signal<any[]>([]);
  newMessage = signal<string>('');

  currentPage = signal<number>(0);
  hasMoreMessages = signal<boolean>(true);
  isLoadingMore = signal<boolean>(false);

  isLoadingChats = signal<boolean>(true);

  selectedChat = computed(() => {
    const id = this.selectedChatId();
    return this.chats().find(c => c.id === id) || null;
  });

  myUserId = this.authService.currentUser()?.id;

  constructor() {}

  ngOnInit() {
    this.loadChats();

    this.wsService.connect();

    this.wsService.messageSubject.subscribe((nuevoMensaje) => {
      const chatIdActual = this.selectedChatId();

      // 1. Si el mensaje es del chat que tengo abierto, lo añado a la pantalla principal
      if (chatIdActual === nuevoMensaje.chatId) {
        this.currentMessages.update(mensajesAnteriores => [...mensajesAnteriores, nuevoMensaje]);
        setTimeout(() => this.scrollToBottom(), 100);
      }

      // 2. Actualizamos la lista de chats de la izquierda (último mensaje y punto rojo)
      this.chats.update(listaActual => listaActual.map(chat => {
        if (chat.id === nuevoMensaje.chatId) {
          return {
            ...chat,
            lastMessage: nuevoMensaje.content,
            hasUnread: chat.id !== chatIdActual
          };
        }
        return chat;
      }));
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('chatId');
      if (id) {
        this.selectChat(id);
      }
    });

    const user = this.authService.currentUser();

    if (user && user.id) {
      this.myUserId = user.id;
    } else {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const payloadBase64 = token.split('.')[1];
          const payloadDecoded = JSON.parse(atob(payloadBase64));
          this.myUserId = payloadDecoded.sub;
        } catch (e) {
          console.error('Error al decodificar token en el plan B', e);
        }
      }
    }
  }

  ngOnDestroy() {
    this.wsService.unsubscribeFromCurrentChat();
  }

  loadChats() {
    this.isLoadingChats.set(true);

    this.chatService.getUserChats().subscribe({
      next: (data) => {
        this.chats.set(data);
        this.isLoadingChats.set(false);
      },
      error: (err) => {
        console.error('Error cargando la lista de chats', err);
        this.isLoadingChats.set(false);
      }
    });
  }

  selectChat(chatId: string) {
    this.selectedChatId.set(chatId);

    // Quitamos el punto rojo de notificación al abrir el chat
    this.chats.update(lista => lista.map(c =>
      c.id === chatId ? { ...c, hasUnread: false } : c
    ));

    this.router.navigate(['/messages', chatId], { replaceUrl: true });

    this.currentMessages.set([]);
    this.currentPage.set(0);
    this.hasMoreMessages.set(true);

    this.loadMessages();

    setTimeout(() => {
      this.wsService.subscribeToChat(chatId);
    }, 500);
  }

  loadMessages() {
    if (!this.hasMoreMessages() || this.isLoadingMore()) return;

    this.isLoadingMore.set(true);
    const chatId = this.selectedChatId();
    if (!chatId) return;

    const container = this.myScrollContainer?.nativeElement;
    const previousScrollHeight = container ? container.scrollHeight : 0;

    this.chatService.getChatMessages(chatId, this.currentPage()).subscribe({
      next: (newMessages) => {
        const orderedMessages = [...newMessages].reverse();

        if (newMessages.length < 50) {
          this.hasMoreMessages.set(false);
        }

        if (this.currentPage() === 0) {
          this.currentMessages.set(orderedMessages);
          setTimeout(() => this.scrollToBottom(), 100);
        } else {
          this.currentMessages.update(old => [...orderedMessages, ...old]);

          setTimeout(() => {
            if (container) {
              container.scrollTop = container.scrollHeight - previousScrollHeight;
            }
          }, 0);
        }

        this.currentPage.update(p => p + 1);
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
    if (!text || !this.selectedChatId()) return;

    const chatId = this.selectedChatId();
    const userId = this.myUserId;

    if (!text || !chatId || !userId) return;

    this.wsService.sendMessage(chatId, userId, text);
    this.newMessage.set('');
  }

  private scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }
}
