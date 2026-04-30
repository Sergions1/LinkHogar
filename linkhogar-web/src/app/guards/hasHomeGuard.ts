import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {AuthService} from '../services/auth/auth.service';
import Swal from 'sweetalert2';

export const hasHomeGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUser();


  if (currentUser && currentUser.homeId) {
    return true;
  } else {
    router.navigate(['/']);
    Swal.fire({
      icon: 'warning',
      title: 'Acceso Denegado',
      text: 'Actualmente no perteneces a ningún hogar.',
      confirmButtonColor: 'var(--color-acento)',
      confirmButtonText: 'Entendido'
    });
    return false;
  }
};
