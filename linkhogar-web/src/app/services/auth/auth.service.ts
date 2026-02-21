import { Injectable, signal, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {jwtDecode} from 'jwt-decode';
import {environment} from '../../../environments/environment';

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
}
