import { Injectable, inject } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import {lastValueFrom, Subject} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserNotification} from '../../Models/Notification/UserNotification';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private currentSubscription: StompSubscription | null = null;
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/chat`;

  // Este Subject es un "megáfono". Cuando llegue un mensaje, avisaremos por aquí.
  public messageSubject = new Subject<any>();
  public notificationSubject = new Subject<UserNotification>();

  public expenseSubject = new Subject<void>();
  private homeExpenseSubscription: StompSubscription | null = null;

  constructor() {
    this.stompClient = new Client({
      reconnectDelay: 5000, // Si se cae internet, intenta reconectar cada 5s
      debug: (str) => {
        // console.log(str);
      }
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Conectado a WebSockets', frame);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Error STOMP:', frame.headers['message']);
    };
  }

  // 1. Abrir la conexión global
  async connect() {
    if (this.stompClient.active) return;

    try {
      const token = localStorage.getItem('token');

      // Si por algún motivo no hay token, no podemos conectar (el usuario cerró sesión)
      if (!token) {
        console.warn('No hay token en localStorage, cancelando conexión WebSocket');
        return;
      }
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      const response = await lastValueFrom(
        this.http.post<{ ticket: string }>(`${this.apiUrl}/ticket`,[], {headers})
      );

      // ✅ LA URL CON EL TICKET SE ASIGNA AQUÍ
      this.stompClient.webSocketFactory = () => {
        return new (SockJS as any)(`${environment.apiUrl}/ws-chat?ticket=${response.ticket}`);
      };

      this.stompClient.onConnect = (frame) => {
        console.log('✅ Conectado a WebSockets Global');

        // 🚀 NUEVO: Nos suscribimos a nuestro canal de notificaciones personales
        const token = localStorage.getItem('token');
        if (token) {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const myId = payload.sub; // Asegúrate de que esto cuadra con tu JWT

          this.stompClient.subscribe(`/topic/user.${myId}`, (message) => {
            const nuevaNotificacion = JSON.parse(message.body);
            // Avisamos al resto de la app (el Header) de que hay algo nuevo
            this.notificationSubject.next(nuevaNotificacion);
          });
        }
      };

      this.stompClient.activate();

    } catch (error) {
      console.error('Error al pedir el ticket:', error);
    }
  }

  // 2. Cerrar la conexión (Al hacer Logout)
  disconnect() {
    if (this.stompClient.active) {
      this.stompClient.deactivate();
    }
  }

  // 3. Suscribirse a un chat específico (Cuando entras a una sala)
  subscribeToChat(chatId: string) {
    if (!this.stompClient.connected) {
      console.warn('Stomp client no está activo aún.');
      setTimeout(() => this.subscribeToChat(chatId), 300);
      return;
    }

    // Si ya estábamos en otro chat, nos desuscribimos primero para no cruzar cables
    this.unsubscribeFromCurrentChat();

    this.currentSubscription = this.stompClient.subscribe(`/topic/chat.${chatId}`, (message) => {
      // Cuando Spring Boot hace "messagingTemplate.convertAndSend", llega aquí
      const parsedMessage = JSON.parse(message.body);
      this.messageSubject.next(parsedMessage); // Avisamos al componente
    });
  }

  unsubscribeFromCurrentChat() {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
      this.currentSubscription = null;
    }
  }

  // 4. Enviar un mensaje
  sendMessage(chatId: string, senderId: string, content: string) {
    if (this.stompClient.active) {
      this.stompClient.publish({
        destination: `/app/chat/${chatId}/sendMessage`,
        body: JSON.stringify({ senderId, content })
      });
    } else {
      console.error('No se puede enviar el mensaje, WebSocket desconectado.');
    }
  }

}
