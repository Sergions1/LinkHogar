import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { AuthService } from '../../../services/auth/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-verify',
  standalone: true,
  templateUrl: './verify.html',
  styleUrl: './verify.scss'
})
export class Verify implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private authService = inject(AuthService);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];

      if (token) {
        // Magia aquí: Limpiamos la URL en el navegador del usuario al instante
        this.location.replaceState('/verify');

        this.verifyAccount(token);
      } else {
        this.showError('Enlace inválido. No se encontró el token de verificación.');
      }
    });
  }

  private verifyAccount(token: string) {
    // Angular hace la llamada limpia al backend: /auth/verify/12345
    this.authService.verifyEmail(token).subscribe({
      next: (response) => {
        Swal.fire({
          icon: 'success',
          title: '¡Cuenta verificada!',
          text: response,
          confirmButtonColor: '#0d6efd'
        }).then(() => {
          this.router.navigate(['/login']);
        });
      },
      error: (err) => {
        this.showError(err.error || 'El enlace ha expirado o es inválido.');
      }
    });
  }

  private showError(message: string) {
    Swal.fire({
      icon: 'error',
      title: 'Error de verificación',
      text: message,
      confirmButtonColor: '#0d6efd'
    }).then(() => {
      this.router.navigate(['/']);
    });
  }
}
