import {Component, effect, inject} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {AuthService} from '../../../services/auth/auth.service';
import {HomeService} from '../../../services/home/home-service';

@Component({
  selector: 'app-home-layout',
    imports: [
        RouterLink,
        RouterLinkActive,
        RouterOutlet
    ],
  templateUrl: './home-layout.html',
  styleUrl: './home-layout.scss',
})
export class HomeLayout {
  isCollapsed = true;
  authService = inject(AuthService);
  homeService = inject(HomeService);

  constructor() {
    effect(() => {
      const homeId = this.authService.currentUser()?.homeId;
      if (homeId) {
        // Disparamos la carga. El servicio se encarga de no repetirla si ya hay datos.
        this.homeService.loadMembers(homeId);
      }
    });
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}
