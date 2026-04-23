import { Injectable, inject } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {DashboardStatsResponse} from '../../Models/Admin/DashboardStatsResponse';
import {CreateUserByAdminResponse} from '../../Models/Admin/CreateUserByAdminResponse';
import {CreateUserByAdminRequest} from '../../Models/Admin/CreateUserByAdminRequest';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {HouseResponse} from '../../Models/Houses/HouseResponse';

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

  setHouseStatus(houseId: string, status: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.put<void>(`${environment.apiUrl}/houses/${houseId}/status`, { status }, { headers });
  }

  // admin-service.ts
  deleteHouse(houseId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.delete<void>(`${environment.apiUrl}/houses/${houseId}`, { headers });
  }

  getPendingHouses(page: number = 0, size: number = 20): Observable<PageResponse<HouseResponse>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}/houses/pending?page=${page}&size=${size}`, { headers });
  }

  deleteReport(reportId: string, archiveHouse: boolean): Observable<void>{
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const params = new HttpParams().set('archiveHouse', archiveHouse);
    return this.http.post<void>(`${environment.apiUrl}/houses/houseReport/delete/${reportId}`, null ,{ headers, params });
  }
}
