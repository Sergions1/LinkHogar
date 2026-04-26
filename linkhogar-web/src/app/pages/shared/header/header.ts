import {Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {NavigationEnd, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../services/auth/auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {filter, Subscription} from 'rxjs';
import {adminGuard} from '../../../guards/admin.guard';
import {UserService} from '../../../services/user/user-service';
import {NotificationService} from '../../../services/notification/notification-service';
import {UserNotification} from '../../../Models/Notification/UserNotification';
import {DatePipe} from '@angular/common';
import {WebSocketService} from '../../../services/chat/web-socket-service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FontAwesomeModule, RouterLink, DatePipe],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit, OnDestroy {
  public authService = inject(AuthService);
  private router = inject(Router);
  public userService = inject(UserService);
  private notificationService = inject(NotificationService);

  isAdminRoute: boolean = false; //Flag para cambiar de color para el panel de administración
  private routerSub!: Subscription; // Para guardar la suscripción y limpiarla luego
  unreadNotifications = signal<UserNotification[]>([]); //Signal para notificaciones no leidas

  private wsService = inject(WebSocketService);
  private wsNotificationSub!: Subscription;

  ngOnInit() {
    this.checkAdminRoute(this.router.url);

    //Suscripcion para escuchar cada vez que el usuario navega
    this.routerSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.checkAdminRoute(event.urlAfterRedirects);
    })

    if (this.authService.isLoggedIn()) {
      this.loadNotifications();
    }

    if (this.authService.isLoggedIn()) {
      this.loadNotifications();

      this.wsNotificationSub = this.wsService.notificationSubject.subscribe((nuevaNotif) => {
        // La añadimos a la lista (al principio para que salga la primera)
        this.unreadNotifications.update(lista => [nuevaNotif, ...lista]);
      });
    }
  }

  ngOnDestroy() {
    if(this.routerSub) {
      this.routerSub.unsubscribe(); //Destruimos para evitar fugas de memoria
    }
    if(this.wsNotificationSub) {
      this.wsNotificationSub.unsubscribe();
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

  loadNotifications() {
    this.notificationService.getUnreadNotifications().subscribe({
      next: (data) => this.unreadNotifications.set(data),
      error: (err) => console.error('Error cargando notificaciones', err)
    });
  }

  markAsRead(notification: UserNotification, event: Event) {
    event.stopPropagation(); // Evita que el panel se cierre al hacer clic
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        // Filtramos la notificación leída para que desaparezca al instante
        this.unreadNotifications.update(list => list.filter(n => n.id !== notification.id));
      },
      error: (err) => console.error('Error al marcar como leída', err)
    });
  }


}
