import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../../../services/auth/auth.service';
import {UserService} from '../../../services/user/user-service';
import {UserResponse} from '../../../Models/Users/UserResponse';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  user: UserResponse | null = null;

  userEmail: string | null = '';
  userRole: string | null = '';

  // Datos simulados (Mocks). ¡Luego los traeremos de Spring Boot!
  stats = {
    usuariosTotales: 142,
    casasPendientes: 5,
    casasPublicadas: 38
  };

  ngOnInit() {
    this.user = this.authService.currentUser();
  }
}
