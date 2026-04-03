import {Component, inject, signal} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service'; // Ajusta tu ruta
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  showPassword = signal(false);

  registerForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    phone: [''],
    mail: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    fecha_nac: ['']
  });


  togglePassword() {
    this.showPassword.update(v => !v);
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      Swal.fire({
        icon: 'error',
        title: 'Formulario incompleto',
        text: 'Por favor, revisa los campos obligatorios marcados en rojo.',
        confirmButtonColor: '#0d6efd'
      });
      return;
    }

    const userData = this.registerForm.value;

    // Llamada real al backend
    this.authService.register(userData).subscribe({
      next: (response) => {
        Swal.fire({
          icon: 'success',
          title: '¡Casi listo!',
          text: response, // Muestra el mensaje del backend ("Por favor, revisa tu correo...")
          confirmButtonColor: '#0d6efd'
        }).then(() => {
          this.router.navigate(['/']); // Redirigimos al inicio o donde prefieras
        });
        this.registerForm.reset();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error en el registro',
          text: err.error || 'No se ha podido completar el registro.',
          confirmButtonColor: '#0d6efd'
        });
      }
    });
  }

  isInvalidField(field: string): boolean {
    const control = this.registerForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
