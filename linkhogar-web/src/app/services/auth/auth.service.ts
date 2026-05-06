import {Injectable, signal, inject, computed} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {jwtDecode} from 'jwt-decode';
import {environment} from '../../../environments/environment';
import {UserResponse} from '../../Models/Users/UserResponse';

/**
 * Servicio encargado de gestionar la autenticación de la aplicación.
 * Maneja el inicio de sesión, cierre de sesión, y la validación y decodificación de tokens JWT.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient); //Inyectamos cliente HTTP

  private apiUrl = `${environment.apiUrl}/auth`;

  isLoggedIn = signal<boolean>(this.hasValidToken());
  currentUser = signal<UserResponse | null>(null);
  favouriteIds = signal<Set<string>>(new Set());
  haveHome = computed(() => {
    const user = this.currentUser(); //Cada vez que currentUser Cambia se comprueba de nuevo

    return user != null && user.homeId != null;
  });

  constructor() {
    if (this.hasValidToken()) {
      this.fetchCurrentUser();
    }
  }


  /**
   * Autentica a un usuario enviando sus credenciales al servidor.
   * Si la petición es exitosa, intercepta la respuesta para almacenar el token JWT
   * en el localStorage y actualiza el estado global de autenticación a verdadero.
   *
   * @param {any} credentials - Objeto que contiene las credenciales del usuario (generalmente email y contraseña).
   * @returns {Observable<string>} Un observable que emite el token JWT en formato de texto plano.
   */
  login(credentials: any): Observable<string>{
    return this.http.post(`${this.apiUrl}/login`, credentials,{
      responseType: "text"
    }).pipe(
      tap((token) => {
        localStorage.setItem('token', token);
        this.isLoggedIn.set(true);
        this.fetchCurrentUser();
      })
    );
  }

  /**
   * Cierra la sesión del usuario actual en el cliente.
   * Elimina el token de seguridad del localStorage y cambia el estado de la señal
   * de autenticación a falso para reflejar el cierre de sesión en toda la app.
   */
  logout(): void {
    localStorage.removeItem('token');
    this.isLoggedIn.set(false);
  }

  /**
   * Verifica internamente si el token almacenado existe y aún no ha expirado.
   * Utiliza la librería `jwt-decode` para leer la propiedad 'exp' (fecha de expiración) del payload del token.
   *
   * @private
   * @returns {boolean} `true` si hay un token guardado y su fecha de expiración es mayor a la fecha actual; `false` en caso contrario o si ocurre un error al decodificar.
   */
  private hasValidToken(): boolean{
    const token = localStorage.getItem('token');
    if (!token) {return false}

    try{
      const decoded: any = jwtDecode(token);
      const expirationDate = decoded.exp * 1000;
      return expirationDate > Date.now();
    }catch(error){
      console.error("Error decodificando token: "+error);
      return false;
    }

  }

  /**
   * Extrae el identificador del usuario (generalmente el correo electrónico) del token JWT almacenado.
   * Lee la propiedad 'sub' (subject) del payload del token.
   *
   * @returns {string | null} El correo electrónico/subject del usuario, o `null` si no hay token o este es inválido.
   */
  getUserEmail(): string | null {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.sub;
    } catch (error) {
      return null;
    }
  }

  /**
   * Obtiene la información del perfil del usuario autenticado actualmente desde el servidor.
   * Realiza una petición GET al endpoint '/me' y actualiza la señal `currentUser` con la respuesta,
   * o la establece en `null` en caso de error.
   */
  fetchCurrentUser() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    this.http.get<UserResponse>(`${environment.apiUrl}/users/currentUser`, { headers }).subscribe({
      next: (user) => {
        this.currentUser.set(user)
        this.fetchFavouriteIds(user.id.toString());
      },
      error: () => this.currentUser.set(null)
    });
  }

  verifyEmail(token: string) {
    // Usamos responseType: 'text' porque el backend devuelve un String plano, no un JSON
    return this.http.get(`${this.apiUrl}/verify/${token}`, { responseType: 'text' });
  }

  register(userData: any){
    return this.http.post(`${this.apiUrl}/register`, userData , { responseType: 'text' });
  }

  requestPasswordCode(mail: string){
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.post(`${this.apiUrl}/request-password-code`, {mail}, { headers });
  }

  verifyPasswordCode(payload: { mail: string, code: string }) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    return this.http.post(`${this.apiUrl}/verify-password-code`, payload, { headers , responseType: 'text' });
  }

  /**
   * Carga la lista de IDs de viviendas favoritas del usuario.
   */
  fetchFavouriteIds(userId: string) {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    // Ajusta esta URL a tu endpoint real de Java
    this.http.get<string[]>(`${environment.apiUrl}/users/favourites/ids/${userId}`, { headers })
      .subscribe({
        next: (ids) => this.favouriteIds.set(new Set(ids)),
        error: (err) => console.error("Error al cargar favoritos:", err)
      });
  }

  /**
   * Verifica si una vivienda específica es favorita consultando el Signal local.
   * Eficiencia O(1) - Sin llamadas al servidor.
   */
  isFavorite(houseId: string): boolean {
    return this.favouriteIds().has(houseId);
  }


  /**
   * Actualiza el estado local de favoritos (Optimistic Update).
   * Se llama desde los componentes tras pulsar el botón de corazón.
   */
  toggleFavoriteLocal(houseId: string) {
    const currentSet = new Set(this.favouriteIds());
    if (currentSet.has(houseId)) {
      currentSet.delete(houseId);
    } else {
      currentSet.add(houseId);
    }
    this.favouriteIds.set(currentSet);
  }

}
