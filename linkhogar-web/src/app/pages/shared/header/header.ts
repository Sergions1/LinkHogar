import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../services/auth/auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  public authService = inject(AuthService);
  private router = inject(Router);

 login(){
   this.router.navigate(['/login']);
 }

 logOut(){
   this.authService.logout();

   this.router.navigate(['/']);
 }

 publicate() {
    var logged = this.authService.isLoggedIn();

    if(logged){
      this.router.navigate(['/publicar-anuncio']);
    }else{
      this.login();
    }
 }




}
