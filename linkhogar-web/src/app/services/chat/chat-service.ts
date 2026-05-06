import {inject, Injectable, signal} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/chat`;

  // === CACHÉ EN MEMORIA PARA EL CHAT DEL HOGAR ===
  homeChatId = signal<string | null>(null);
  homeChatMessages = signal<any[]>([]);
  hasMoreHomeMessages = signal<boolean>(true);
  currentHomeMessagePage = signal<number>(0);

  /**
   * Inicia una nueva conversación de chat entre un usuario interesado y el dueño de una casa.
   * @param houseId ID de la vivienda sobre la que se consulta.
   * @param ownerId ID del propietario de la vivienda.
   * @param initialMessage Primer mensaje para abrir la conversación.
   * @returns Observable con el ID del chat creado.
   */
  initiateChat(houseId: string, ownerId: string, initialMessage: string): Observable<{chatId: string}> {
    if (!houseId || !ownerId || !initialMessage) {
      return throwError(() => new Error('Faltan datos obligatorios para iniciar el chat'));
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<{chatId: string}>(`${this.apiUrl}/initiate`,
      { houseId, ownerId, initialMessage },
      { headers }
    );
  }

  /**
   * Obtiene la lista de todos los chats en los que el usuario actual participa
   * (ya sea como interesado o como propietario).
   * @returns Observable con un array de objetos de chat.
   */
  getUserChats(): Observable<any[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.apiUrl}/my-chats`, { headers });
  }

  getChatMessages(chatId: string, page: number = 0, size: number = 30): Observable<any[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error("Intento de cargar mensajes sin token en localStorage");
      return throwError(() => new Error("No hay token de autenticación"));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<any[]>(`${this.apiUrl}/${chatId}/messages?page=${page}&size=${size}`, { headers });
  }

  getHomeChat(homeId: string): Observable<{chatId: string}> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<{chatId: string}>(`${this.apiUrl}/home/${homeId}`, { headers });
  }
}
