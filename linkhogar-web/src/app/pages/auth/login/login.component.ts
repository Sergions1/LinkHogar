// login.component.ts
import {Component, inject, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../../services/auth/auth.service';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  loginData = { mail: '', password: '' };
  isLoading = signal(false);
  showPassword = signal(false);

  // Errores de validación
  mailError = signal<string | null>(null);
  passwordError = signal<string | null>(null);

  togglePassword() {
    this.showPassword.update(v => !v);
  }

  validate(): boolean {
    let valid = true;
    this.mailError.set(null);
    this.passwordError.set(null);

    if (!this.loginData.mail.trim()) {
      this.mailError.set('El email es obligatorio.');
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.loginData.mail)) {
      this.mailError.set('Introduce un email válido.');
      valid = false;
    }

    if (!this.loginData.password.trim()) {
      this.passwordError.set('La contraseña es obligatoria.');
      valid = false;
    } else if (this.loginData.password.length < 6) {
      this.passwordError.set('La contraseña debe tener al menos 6 caracteres.');
      valid = false;
    }

    return valid;
  }

  onSubmit() {
    if (!this.validate()) return;

    this.isLoading.set(true);
    this.authService.login(this.loginData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.authService.fetchCurrentUser();
        Swal.fire({
          title: '¡Bienvenido!',
          text: 'Has iniciado sesión correctamente.',
          icon: 'success',
          confirmButtonColor: 'var(--color-acento)',
          timer: 1500,
          showConfirmButton: false
        }).then(() => this.router.navigate(['/']));
      },
      error: (error) => {
        this.isLoading.set(false);

        let title = 'Error al iniciar sesión';
        let msg = 'Ha ocurrido un error. Inténtalo de nuevo.';
        let icon: 'error' | 'warning' = 'error';

        // Comprobamos si el error es de cuenta no habilitada (DisabledException)
        // Spring Security suele devolver un 401 o 403 con un mensaje específico o un error "User is disabled"
        const backendError = error.error?.message || error.error || '';

        if (backendError.toString().toLowerCase().includes('disabled') || backendError.toString().toLowerCase().includes('desactivada') || error.status === 403) {
          title = 'Cuenta no verificada';
          msg = 'Por favor, revisa tu correo electrónico y haz clic en el enlace de verificación para activar tu cuenta.';
          icon = 'warning';
        } else if (error.status === 401) {
          msg = 'Email o contraseña incorrectos.';
        }

        Swal.fire({
          title: title,
          text: msg,
          icon: icon,
          confirmButtonColor: 'var(--color-acento)',
          confirmButtonText: 'Entendido'
        });
      }
    });
  }
}
