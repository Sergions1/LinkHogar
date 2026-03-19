import {AuthService} from '../services/auth/auth.service';
import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {UserService} from '../services/user/user-service';

export const adminGuard: CanActivateFn = (route, state) =>{
  const authService = inject(AuthService);
  const userService = inject(UserService);
  const router = inject(Router);

  const role = userService.getRole()

  if (authService.isLoggedIn() && (role === 'Admin' || role === 'LinkHogar')) {
    return true;
  }

  router.navigate(['/']);
  return false;
}

export const masterGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const userService = inject(UserService);

  if (authService.isLoggedIn() && userService.getRole() === 'LinkHogar') {
    return true;
  }

  router.navigate(['/admin/dashboard']);
  return false;
};
