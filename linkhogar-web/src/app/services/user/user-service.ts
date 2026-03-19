import {inject, Injectable, signal} from '@angular/core';
import {jwtDecode} from 'jwt-decode';
import {UserResponse} from '../../Models/Users/UserResponse';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {


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



}
