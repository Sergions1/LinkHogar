import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {UserNotification} from '../../Models/Notification/UserNotification';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private http = inject(HttpClient);
  // Ajusta la URL base según cómo la tengas en tu environment (ej: http://localhost:8080/api/notifications)
  private apiUrl = `${environment.apiUrl}/notifications`;

  /**
   * Obtiene la lista de notificaciones no leídas del usuario autenticado
   */
  getUnreadNotifications(): Observable<UserNotification[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<UserNotification[]>(`${this.apiUrl}/unread`, { headers });
  }

  /**
   * Marca una notificación específica como leída en la base de datos
   * @param notificationId El ID de la notificación
   */
  markAsRead(notificationId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Usamos PATCH porque es una actualización parcial (solo cambiamos el isRead a true)
    return this.http.patch<void>(`${this.apiUrl}/${notificationId}/read`, {}, { headers });
  }
}
