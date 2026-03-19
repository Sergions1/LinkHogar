import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterLinkActive} from '@angular/router';
import {filter, Subscription} from 'rxjs';

@Component({
  selector: 'app-footer',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer implements OnInit, OnDestroy{
  private router = inject(Router);

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

}
