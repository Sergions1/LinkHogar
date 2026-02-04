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

  constructor(private router: Router) { }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)).subscribe((event: any) => {
        this.showlayout = !event.url.includes("/login"); //Si la ruta es login no se muestra el layout
      });
  }

  protected readonly title = signal('linkhogar-front');
}
