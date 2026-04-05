import { Routes } from '@angular/router';
import {LoginComponent} from './pages/auth/login/login.component';
import {LandingComponent} from './pages/shared/landing/landing.component';
import {Explore} from './pages/explore/explore';
import {Detail} from './pages/house/detail/detail';
import {Infoannouncement} from './pages/Announcement/infoaunnouncement/infoannouncement.component';
import {Create} from './pages/house/create/create';
import {AdminLayoutComponent} from './pages/Admin/admin-layout/admin-layout';
import {adminGuard} from './guards/admin.guard';
import {NotFound} from './pages/shared/error/not-found/not-found';
import {DashboardComponent} from './pages/Admin/dashboard/dashboard';
import {AdminHousesComponent} from './pages/Admin/HouseGrid/admin-houses/admin-houses';
import {AdminUsersComponent} from './pages/Admin/usersgrid/admin-users/admin-users';
import {AdminRequests} from './pages/Admin/HouseGrid/admin-requests/admin-requests';
import {Register} from './pages/auth/register/register';
import {Verify} from './pages/auth/verify/verify';
import {Profile} from './pages/user/profile/profile';
import {authGuard} from './guards/authGuard';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: Register},
  {path: 'verify', component: Verify},
  {path:"", component: LandingComponent},
  {path:"explore", component: Explore},
  {path: 'explore/:provincia/:municipio', component: Explore },
  {path: 'inmueble/:titulo/:id', component: Detail},
  {path:"publicar-anuncio", component: Infoannouncement},
  {path:"perfil", component: Profile, canActivate: [authGuard]},
  {path:"new-announcement", component: Create},
  {
    path: 'admin',
    component: AdminLayoutComponent, // El Layout padre
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // Si entran a /admin, los manda al dashboard
      { path: 'dashboard', component: DashboardComponent },
      { path: 'houses', component: AdminHousesComponent} ,
      { path: 'houses/requests', component: AdminRequests},
      { path: 'users', component: AdminUsersComponent }
    ]
  },
  { path: '**', component: NotFound }
];
