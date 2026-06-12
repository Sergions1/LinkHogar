import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../../../services/auth/auth.service';
import {UserService} from '../../../services/user/user-service';
import {UserResponse} from '../../../Models/Users/UserResponse';
import {AdminService} from '../../../services/admin/admin-service';
import {DashboardStatsResponse} from '../../../Models/Admin/DashboardStatsResponse';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  public adminService = inject(AdminService);

  user = this.authService.currentUser;

  stats = this.adminService.stats;

  ngOnInit() {
    this.adminService.preloadAdminData();

    if (!this.stats()) {
      this.adminService.getDashboardStats().subscribe({
        next: (data) => {
          console.log('Stats recibidas:', data);
          this.stats.set(data);
        },
        error: (err) => console.error("Error cargando estadísticas", err)
      });
    }
  }
}
