import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from '../../../services/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-infoannouncement',
  imports: [],
  templateUrl: './infoannouncement.html',
  styleUrl: './infoannouncement.scss',
})
export class Infoannouncement implements OnInit {

  public authService = inject(AuthService);
  public router = inject(Router);

  ngOnInit() {
    const auth = this.authService.isLoggedIn();

    if(!auth){
      this.router.navigate(['/login']);
    }
  }

  newAnnouncement(): void {
    this.router.navigate(['/new-announcement']);
  }

}
