import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {AuthService} from '../services/auth/auth.service';
import Swal from 'sweetalert2';

export const hasHomeGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if(!authService.isLoggedIn()){
    router.navigate(['/login']);
    return false;
  }

  const currentUserHomeId = authService.currentUser()?.homeId;


//todo revisar porque no aparece el homeId en el currentUser
  console.log("home id de Usuario actual: "+ currentUserHomeId);
  debugger;

  if (currentUserHomeId) {
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
