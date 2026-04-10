import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import {AuthService} from '../services/auth/auth.service';


export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  const token = localStorage.getItem('token');

  if (token) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};
