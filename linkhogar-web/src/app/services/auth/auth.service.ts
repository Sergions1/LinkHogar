import { Injectable, signal, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {jwtDecode} from 'jwt-decode';
import {environment} from '../../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient); //Inyectamos cliente HTTP

  private apiUrl = `${environment.apiUrl}/auth`;

  isLoggedIn = signal<boolean>(this.hasValidToken());

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

  logout(): void {
    localStorage.removeItem('token');
    this.isLoggedIn.set(false);
  }

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
