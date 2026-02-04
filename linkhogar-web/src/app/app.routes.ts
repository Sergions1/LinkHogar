import { Routes } from '@angular/router';
import {LoginComponent} from './pages/auth/login/login.component';
import {LandingComponent} from './pages/shared/landing/landing.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path:"", component: LandingComponent},
];
