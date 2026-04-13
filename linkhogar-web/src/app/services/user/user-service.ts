import {inject, Injectable} from '@angular/core';
import {jwtDecode} from 'jwt-decode';
import {UserResponse} from '../../Models/Users/UserResponse';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';

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

  /**
   * Sube un nuevo avatar para un usuario específico.
   * @param userId El ID del usuario para el que se sube el avatar.
   * @param formData El objeto FormData que contiene el archivo de imagen.
   * @returns Un Observable con la respuesta del servidor.
   */
  uploadAvatar(userId: string, formData: FormData) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.post(`${this.apiUrl}/uploadAvatar/${userId}`, formData, { headers });
  }

  /**
   * Actualiza la información del perfil de un usuario.
   * @param userId El ID del usuario a actualizar.
   * @param data Un objeto con los datos del perfil a modificar.
   * @returns Un Observable con la respuesta de texto del servidor.
   */
  updateProfile(userId: string, data: any) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.put(`${this.apiUrl}/update/${userId}`, data, { headers, responseType: 'text' });
  }

  /**
   * Cambia la contraseña del usuario autenticado actualmente.
   * @param data Un objeto que contiene la contraseña actual y la nueva.
   * @returns Un Observable con la respuesta de texto del servidor.
   */
  changePassword(data: any) {
    const token = localStorage.getItem("token");
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.put(`${this.apiUrl}/change-password`, data, { headers, responseType: 'text' });
  }

  /**
   * Añade un inmueble a la lista de favoritos de un usuario.
   * @param userId El ID del usuario.
   * @param houseId El ID del inmueble a añadir.
   * @returns Un Observable con la respuesta de texto del servidor.
   */
  addFavouriteHouse(userId: string, houseId: string) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.post(`${this.apiUrl}/addFavourite/${userId}/${houseId}`, {}, { headers, responseType: 'text'});
  }

  /**
   * Elimina un inmueble de la lista de favoritos de un usuario.
   * @param userId El ID del usuario.
   * @param houseId El ID del inmueble a eliminar.
   * @returns Un Observable que completa la operación.
   */
  deleteFavouriteHosue(userId: string, houseId: string) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.delete(`${this.apiUrl}/deleteFavourite/${userId}/${houseId}`, { headers });
  }

  /**
   * Obtiene una lista paginada de los inmuebles favoritos de un usuario.
   * @param userId El ID del usuario del que se quieren obtener los favoritos.
   * @param page El número de página a obtener.
   * @param size El número de elementos por página.
   * @returns Un Observable con una página de respuestas de tarjetas de inmuebles.
   */
  getPaginatedFavourites(userId: string, page: number, size: number = 10): Observable<PageResponse<HouseCardResponse>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<HouseCardResponse>>(`${this.apiUrl}/getPaginatedFavourites/${userId}`, {headers, params });
  }

}
