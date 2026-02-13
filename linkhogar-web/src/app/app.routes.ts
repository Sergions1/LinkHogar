import { Routes } from '@angular/router';
import {LoginComponent} from './pages/auth/login/login.component';
import {LandingComponent} from './pages/shared/landing/landing.component';
import {Explore} from './pages/explore/explore';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path:"", component: LandingComponent},
  {path:"explore", component: Explore},
];
