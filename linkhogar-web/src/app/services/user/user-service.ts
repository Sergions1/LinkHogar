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

  /**
   * Obtiene el rol del usuario actual decodificando el JWT almacenado en el localStorage.
   * @returns El nombre del rol o null si no existe el token o es inválido.
   */
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

  /**
   * Verifica si el usuario actual tiene privilegios de administrador.
   * @returns Verdadero si el rol es 'Admin' o 'LinkHogar'.
   */
  isAdmin(): boolean{
    const role = this.getRole();
    return role === 'Admin' || role === 'LinkHogar';
  }

  /**
   * Obtiene una lista paginada de usuarios con filtros opcionales.
   * @param page Número de página (por defecto 0).
   * @param size Cantidad de elementos por página (por defecto 10).
   * @param search Término de búsqueda opcional para filtrar por nombre o email.
   * @param role Filtro opcional por rol de usuario.
   * @param enabled Filtro opcional para obtener usuarios activos o inactivos.
   * @returns Un Observable con la respuesta paginada de usuarios.
   */
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

  /**
   * Alterna el estado de habilitación (activo/inactivo) de un usuario.
   * @param userId Identificador único del usuario.
   * @returns Un Observable que completa la operación.
   */
  toggleUserEnabled(userId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.patch<void>(`${this.apiUrl}/${userId}/toggle-enabled`, null, { headers });
  }

  /**
   * Elimina permanentemente un usuario del sistema.
   * @param userId Identificador único del usuario a eliminar.
   * @returns Un Observable que completa la operación.
   */
  deleteUser(userId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.delete<void>(`${this.apiUrl}/${userId}`, { headers });
  }

  uploadAvatar(userId: string, formData: FormData) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.post(`${this.apiUrl}/uploadAvatar/${userId}`, formData, { headers });
  }

  updateProfile(userId: string, data: any) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.put(`${this.apiUrl}/update/${userId}`, data, { headers, responseType: 'text' });
  }

  changePassword(data: any) {
    const token = localStorage.getItem("token");
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.put(`${this.apiUrl}/change-password`, data, { headers, responseType: 'text' });
  }
}
