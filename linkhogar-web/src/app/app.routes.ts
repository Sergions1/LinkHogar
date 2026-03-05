import { Routes } from '@angular/router';
import {LoginComponent} from './pages/auth/login/login.component';
import {LandingComponent} from './pages/shared/landing/landing.component';
import {Explore} from './pages/explore/explore';
import {Detail} from './pages/house/detail/detail';
import {Infoannouncement} from './pages/Announcement/infoaunnouncement/infoannouncement.component';
import {Create} from './pages/house/create/create';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path:"", component: LandingComponent},
  {path:"explore", component: Explore},
  {path: 'explore/:provincia/:municipio', component: Explore },
  {path: 'inmueble/:titulo/:id', component: Detail},
  {path:"publicar-anuncio", component: Infoannouncement},
  {path:"new-announcement", component: Create},
];
