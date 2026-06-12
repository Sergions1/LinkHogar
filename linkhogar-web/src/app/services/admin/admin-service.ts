import {Injectable, inject, signal} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {DashboardStatsResponse} from '../../Models/Admin/DashboardStatsResponse';
import {CreateUserByAdminResponse} from '../../Models/Admin/CreateUserByAdminResponse';
import {CreateUserByAdminRequest} from '../../Models/Admin/CreateUserByAdminRequest';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {HouseResponse} from '../../Models/Houses/HouseResponse';
import {UserService} from '../user/user-service';
import {UserResponse} from '../../Models/Users/UserResponse';
import {HouseReport} from '../../Models/Houses/HouseReport';
import { HouseService } from '../house/house-service';
import {SettingsServices} from '../settings/settings-services';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/admin`;
  private userService = inject(UserService);
  private houseService = inject(HouseService);
  private settingsService = inject(SettingsServices);

  // CACHÉ GLOBAL
  stats = signal<DashboardStatsResponse | null>(null);
  pendingHouses = signal<PageResponse<HouseResponse> | null>(null);

  // Nota: Para la sección de anuncios general, usaremos también una señal global
  adminHouses = signal<PageResponse<HouseResponse> | null>(null);
  adminUsers = signal<PageResponse<UserResponse> | null>(null);
  adminReports = signal<PageResponse<HouseReport> | null>(null);


  preloadAdminData(page: number = 0, size: number = 10) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    // 1. Estadísticas
    if (!this.stats()) {
      this.http.get<DashboardStatsResponse>(`${this.apiUrl}/stats`, { headers })
        .subscribe(data => this.stats.set(data));
    }

    // 2. Denuncias pendientes
    if (!this.pendingHouses()) {
      this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}/houses/pending?page=0&size=20`, { headers })
        .subscribe(data => this.pendingHouses.set(data));
    }

    // 3. Usuarios (Llamando al UserService)
    if (!this.adminUsers()) {
      this.userService.getAllUsers(page, size).subscribe(data => {
        this.adminUsers.set(data);
      });
    }

    // 4. Denuncias (Llamando al HouseService)
    if (!this.adminReports()) {
      this.houseService.getAllReports(0, size).subscribe(data => {
        this.adminReports.set(data);
      });
    }

    // 🌟 5. Precargar los anuncios generales
    if (!this.adminHouses()) {
      this.houseService.getPaginatedHouses(0, size).subscribe(data => {
        this.adminHouses.set(data);
      });
    }

    //6. Configuración General (Ajustes de la plataforma)
    // Asumimos que si no hay logo ni hero en la señal, toca cargarlos
    if (!this.settingsService.logoImage() && !this.settingsService.heroImage()) {
      this.settingsService.loadAllSettings().subscribe({
        error: (err) => console.error('Error precargando ajustes generales', err)
      });
    }
  }

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
    return this.http.put<void>(`${environment.apiUrl}/users/update/${userId}`, payload, { headers });
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
