import {inject, Injectable, signal} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {CreateEventRequest, HomeEventResponse} from '../../Models/event/eventModel';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/events`;

  // Guardamos los eventos en memoria (Signal) igual que hiciste con los gastos
  public homeEvents = signal<HomeEventResponse[]>([]);

  getHomeEvents(homeId: string): Observable<HomeEventResponse[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<HomeEventResponse[]>(`${this.apiUrl}/home/${homeId}`, {headers}).pipe(
      tap(events => this.homeEvents.set(events))
    );
  }

  createEvent(request: CreateEventRequest): Observable<{ id: string }> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<{ id: string }>(`${this.apiUrl}/create`, request, {headers});
  }

  updateEvent(eventId: string, request: any): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.put<void>(`${this.apiUrl}/${eventId}`, request, { headers });
  }
}
