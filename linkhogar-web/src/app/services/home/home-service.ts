import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface HomeMember {
  id: string;
  firstName: string;
  lastName: string;
  name: string;
  avatarUrl?: string;
}

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/homes`;

  // SIGNAL: Aquí se guardan los integrantes en caché para toda la app
  members = signal<HomeMember[]>([]);

  loadMembers(homeId: string) {
    // Magia: Solo hacemos la petición HTTP si la lista está vacía
    if (this.members().length === 0) {
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

      this.http.get<HomeMember[]>(`${environment.apiUrl}/homeTasks/${homeId}/members`, { headers })
        .subscribe({
          next: (data) => this.members.set(data),
          error: (err) => console.error('Error al cargar integrantes del hogar', err)
        });
    }
  }
}
