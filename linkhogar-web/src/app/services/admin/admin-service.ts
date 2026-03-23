import { Injectable, inject } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {DashboardStatsResponse} from '../../Models/Admin/DashboardStatsResponse';
import {CreateUserByAdminResponse} from '../../Models/Admin/CreateUserByAdminResponse';
import {CreateUserByAdminRequest} from '../../Models/Admin/CreateUserByAdminRequest';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/admin`;

  getDashboardStats(): Observable<DashboardStatsResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<DashboardStatsResponse>(`${this.apiUrl}/stats`, { headers });
  }

  // admin-service.ts
  createUser(data: CreateUserByAdminRequest): Observable<CreateUserByAdminResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post<CreateUserByAdminResponse>(`${this.apiUrl}/create-user`, data, { headers });
  }

  updateUser(userId: string, data: CreateUserByAdminRequest): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const payload = {
      firstName: data.firstName,
      lastName: data.lastName,
      phone: data.phone,
      fecha_Nac: data.fechaNac ? `${data.fechaNac}T00:00:00` : null,
      role: data.role
    };
    return this.http.put<void>(`${environment.apiUrl}/users/${userId}`, payload, { headers });
  }
}
