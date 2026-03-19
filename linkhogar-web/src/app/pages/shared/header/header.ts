import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {AuthService} from '../../../services/auth/auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {filter, Subscription} from 'rxjs';
import {adminGuard} from '../../../guards/admin.guard';
import {UserService} from '../../../services/user/user-service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit, OnDestroy {
  public authService = inject(AuthService);
  private router = inject(Router);
  public userService = inject(UserService);

  isAdminRoute: boolean = false; //Flag para cambiar de color para el panel de administración
  private routerSub!: Subscription; // Para guardar la suscripción y limpiarla luego

  ngOnInit() {
    this.checkAdminRoute(this.router.url);

    //Suscripcion para escuchar cada vez que el usuario navega
    this.routerSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.checkAdminRoute(event.urlAfterRedirects);
    })
  }

  ngOnDestroy() {
    if(this.routerSub) {
      this.routerSub.unsubscribe(); //Destruimos para evitar fugas de memoria
    }
  }

  private checkAdminRoute(url: string) {
    this.isAdminRoute = url.startsWith('/admin');
  }

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

 admin(){
    if(this.authService.isLoggedIn() && this.userService.isAdmin()){
      this.router.navigate(['/admin']);
    }else{
      this.router.navigate(['/error']);
    }
 }




}
