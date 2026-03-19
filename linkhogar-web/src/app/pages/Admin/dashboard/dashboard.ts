import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../../../services/auth/auth.service';
import {UserService} from '../../../services/user/user-service';
import {UserResponse} from '../../../Models/Users/UserResponse';
import {AdminService} from '../../../services/admin/admin-service';
import {DashboardStatsResponse} from '../../../Models/Admin/DashboardStatsResponse';

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
  private adminService = inject(AdminService);

  user: UserResponse | null = null;

  userEmail: string | null = '';
  userRole: string | null = '';

  // Datos simulados (Mocks). ¡Luego los traeremos de Spring Boot!
  stats: DashboardStatsResponse = {
    totalUsers: 0,
    pendingHouses: 0,
    publishedHouses: 0
  };

  ngOnInit() {
    this.user = this.authService.currentUser();

    this.adminService.getDashboardStats().subscribe({
      next: (data) => {
        console.log('Stats recibidas:', data);
        this.stats = data; // ¡Sobrescribimos los ceros con los datos reales de la BD!
      },
      error: (err) => console.error("Error cargando estadísticas", err)
    });
  }
}
