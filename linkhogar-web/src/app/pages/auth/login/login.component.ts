import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth/auth.service';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {UserService} from '../../../services/user/user-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  loginData = { mail: '', password: '' };
  isLoading = signal(false);
  showPassword = signal(false);

  // Errores de validación
  mailError = signal<string | null>(null);
  passwordError = signal<string | null>(null);

  // --- Recuperación de Contraseña ---
  forgotPasswordStep: 1 | 2 | 3 = 1;
  forgotPasswordForm: FormGroup = this.fb.group({
    mail: ['', [Validators.required, Validators.email]],
    code: [''],
    newPassword: [''],
    confirmPassword: ['']
  }, { validators: this.checkPasswords });

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

  // --- MÉTODOS DE RECUPERACIÓN DE CONTRASEÑA ---

  checkPasswords(group: FormGroup) {
    const pass = group.get('newPassword')?.value;
    const confirmPass = group.get('confirmPassword')?.value;
    if (pass || confirmPass) {
      return pass === confirmPass ? null : { notSame: true };
    }
    return null;
  }

  initForgotPassword() {
    this.forgotPasswordStep = 1;
    this.forgotPasswordForm.reset();
  }

  requestRecoveryCode() {
    const mailControl = this.forgotPasswordForm.get('mail');
    if (mailControl?.valid) {
      this.isLoading.set(true);
      this.authService.requestPasswordCode(mailControl.value).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.forgotPasswordStep = 2;
          this.forgotPasswordForm.get('code')?.setValidators([Validators.required]);
          this.forgotPasswordForm.get('code')?.updateValueAndValidity();
        },
        error: () => {
          this.isLoading.set(false);
          Swal.fire({
            title: 'Error',
            text: 'No se ha podido enviar el código. Verifica que el correo electrónico es correcto y está registrado.',
            icon: 'error',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }

  verifyRecoveryCode() {
    const mailControl = this.forgotPasswordForm.get('mail');
    const codeControl = this.forgotPasswordForm.get('code');

    if (codeControl?.valid) {
      this.isLoading.set(true);
      const data = {
        mail: mailControl?.value,
        code: codeControl?.value
      };

      this.authService.verifyPasswordCode(data).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.forgotPasswordStep = 3;
          this.forgotPasswordForm.get('newPassword')?.setValidators([Validators.required, Validators.minLength(6)]);
          this.forgotPasswordForm.get('confirmPassword')?.setValidators([Validators.required]);
          this.forgotPasswordForm.get('newPassword')?.updateValueAndValidity();
          this.forgotPasswordForm.get('confirmPassword')?.updateValueAndValidity();
        },
        error: () => {
          this.isLoading.set(false);
          codeControl?.setErrors({ invalidCode: true });
        }
      });
    }
  }

  resetPassword() {
    if (this.forgotPasswordForm.valid) {
      this.isLoading.set(true);
      const formValues = this.forgotPasswordForm.value;

      const payload = {
        mail: formValues.mail,
        code: formValues.code,
        newPassword: formValues.newPassword
      };

      // Usamos el NUEVO endpoint público del AuthService
      this.authService.resetPasswordOutside(payload).subscribe({
        next: () => {
          this.isLoading.set(false);
          document.getElementById('closeForgotModal')?.click();
          Swal.fire({
            title: '¡Contraseña actualizada!',
            text: 'Tu contraseña ha sido actualizada con éxito. Ya puedes iniciar sesión.',
            icon: 'success',
            confirmButtonText: 'Entendido',
            confirmButtonColor: 'var(--color-acento)'
          });
        },
        error: () => {
          this.isLoading.set(false);
          Swal.fire({
            title: 'Error',
            text: 'No se pudo actualizar la contraseña. El código puede haber expirado.',
            icon: 'error',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }
}
