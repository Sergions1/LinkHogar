import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {AuthService} from '../services/auth/auth.service';
import Swal from 'sweetalert2';
import {toObservable} from '@angular/core/rxjs-interop';
import {filter, map, take} from 'rxjs';

export const hasHomeGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if(!authService.isLoggedIn()){
    router.navigate(['/login']);
    return false;
  }

  const checkHomeAccess = (homeId: string | undefined): boolean => {
    if (homeId) {
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

  const user = authService.currentUser();

  if (user) {
    return checkHomeAccess(user.homeId);
  }

//todo revisar porque no aparece el homeId en el currentUser

  //Si el usuario es null/undefined (recarga de página F5), ESPERAMOS a que cargue
  return toObservable(authService.currentUser).pipe(
    filter(loadedUser => loadedUser !== null && loadedUser !== undefined), // Congela el guard hasta que el usuario exista
    take(1), // Toma el primer valor válido y se desuscribe para evitar fugas de memoria
    map(loadedUser => checkHomeAccess(loadedUser?.homeId)) // Ejecuta la validación final
  );
};
