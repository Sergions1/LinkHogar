import {Component, OnInit, signal} from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import {Header} from './pages/shared/header/header';
import {Footer} from './pages/shared/footer/footer';
import {filter, take} from 'rxjs';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.component.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  showlayout: boolean = true;
  isDashboard: boolean = false;

  constructor(private router: Router) { }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)).subscribe((event: any) => {
        const currentUrl = event.urlAfterRedirects || event.url;

        this.showlayout = !event.url.includes("/login"); //Si la ruta es login no se muestra el layout

        this.isDashboard = currentUrl.includes("/hogar") || currentUrl.includes("/admin");
      });
  }

  protected readonly title = signal('linkhogar-front');
}
