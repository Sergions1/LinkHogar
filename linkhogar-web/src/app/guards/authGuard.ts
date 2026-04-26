import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import {AuthService} from '../services/auth/auth.service';


export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const token = localStorage.getItem('token');

  const isValid = authService.isLoggedIn();

  if (isValid) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};
