import {inject, Injectable, signal} from '@angular/core';
import {jwtDecode} from 'jwt-decode';
import {UserResponse} from '../../Models/Users/UserResponse';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageResponse} from '../../Models/Shared/PageResponse';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/users`;

  getRole(): string | null {
    // Cambia 'token' por la clave real donde guardes tu JWT
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const decoded: any = jwtDecode(token);
      return decoded.role ?? null;
    } catch (error) {
      return null;
    }

  }

  isAdmin(): boolean{
    const role = this.getRole();
    return role === 'Admin' || role === 'LinkHogar';
  }

  getAllUsers(
    page: number = 0,
    size: number = 10,
    search?: string,
    role?: string,
    enabled?: boolean
  ): Observable<PageResponse<UserResponse>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (search) params = params.set('search', search);
    if (role) params = params.set('role', role);
    if (enabled !== undefined) params = params.set('enabled', enabled);

    return this.http.get<PageResponse<UserResponse>>(`${this.apiUrl}`, { headers, params });
  }

}
