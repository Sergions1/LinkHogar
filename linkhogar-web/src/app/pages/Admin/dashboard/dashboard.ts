import {Component, inject, OnInit, signal} from '@angular/core';
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

  user = this.authService.currentUser;

  userEmail: string | null = '';
  userRole: string | null = '';

  stats = signal<DashboardStatsResponse>({  // 👈 WritableSignal con valores por defecto
    totalUsers: 0,
    pendingHouses: 0,
    publishedHouses: 0
  });

  ngOnInit() {
    this.adminService.getDashboardStats().subscribe({
      next: (data) => {
        console.log('Stats recibidas:', data);
        this.stats.set(data);
      },
      error: (err) => console.error("Error cargando estadísticas", err)
    });
  }
}
