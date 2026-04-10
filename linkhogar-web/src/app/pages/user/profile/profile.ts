import {Component, OnInit, inject, ChangeDetectorRef, effect} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth/auth.service';
import {UserService} from '../../../services/user/user-service';
import Swal from 'sweetalert2';
import {AvatarModal} from '../avatar-modal/avatar-modal';


@Component({
  selector: 'app-profile',
  imports: [
    ReactiveFormsModule,
    AvatarModal
  ],
  templateUrl: './profile.html'
})
export class Profile implements OnInit {
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private authService = inject(AuthService);
  private userService = inject(UserService);

  currentUser = this.authService.currentUser;
  isEditing = false;

  // Estado para el modal de contraseña
  passwordStep: 1 | 2 = 1;

  profileForm: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    phone: [''],
    fecha_Nac: ['']
  });

  passwordForm: FormGroup = this.fb.group({
    code: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required]
  }, { validators: this.checkPasswords });

  constructor() {
    // Escuchamos los cambios en el Signal para sobrevivir al F5
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          phone: user.phone,
          fecha_Nac: user.fechaNac
        });
      }
    });
  }

  ngOnInit() {
    if (!this.currentUser()) {
      this.authService.fetchCurrentUser();
    }
  }

  loadData() {
    const user = this.currentUser();

    if (user) {
      this.profileForm.patchValue({
        firstName: user.firstName,
        lastName: user.lastName,
        phone: user.phone,
        fecha_Nac: user.fechaNac,
      });
    }
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.loadData();
    }
  }

  saveChanges() {
    const userId = this.currentUser()?.id;

    const formValues = this.profileForm.value;

    const payload = {
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      phone: formValues.phone,
      fecha_Nac: formValues.fecha_Nac // <-- Mapeamos de 'fechaNac' a 'fecha_Nac'
    };

    if (this.profileForm.valid && userId) {
      this.userService.updateProfile(userId, payload).subscribe({
        next: () => {
          this.isEditing = false;
          this.authService.fetchCurrentUser();
          this.cdr.detectChanges();
          Swal.fire({
            title: '¡Datos actualizados!',
            text: 'Sus datos han sido actualizados con éxito',
            icon: 'success',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        },
        error: () => {
          Swal.fire({
            title: 'Error',
            text: 'No se pudo actualizar los datos.',
            icon: 'error',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }

  checkPasswords(group: FormGroup) {
    const pass = group.get('newPassword')?.value;
    const confirmPass = group.get('confirmPassword')?.value;
    return pass === confirmPass ? null : { notSame: true };
  }

  iniciarCambioPassword() {
    this.passwordStep = 1;
    this.passwordForm.reset();

    const user = this.currentUser();
    if (user) {
      // Llama a tu servicio para que envíe el email con el código
      this.authService.requestPasswordCode(user.mail).subscribe();
    }
  }

  verifyCode() {
    if (this.passwordForm.get('code')?.valid) {
      const userMail = this.currentUser()?.mail;

      if (!userMail) {
        console.error('No se encontró el email del usuario actual');
        return;
      }

      const data = {
        mail: userMail,
        code: this.passwordForm.get('code')?.value
      }

      this.authService.verifyPasswordCode(data).subscribe({
        next: () => {
          this.passwordStep = 2;
          this.cdr.detectChanges();
        },
        error: () => {
          this.passwordForm.get('code')?.setErrors({ invalidCode: true });
          this.cdr.detectChanges();
        }
      });
    }
  }


  changePassword() {
    const userEmail = this.currentUser()?.mail;

    if (this.passwordForm.valid && userEmail) {
      const payload = {
        mail: userEmail,
        code: this.passwordForm.value.code,
        newPassword: this.passwordForm.value.newPassword
      };

      this.userService.changePassword(payload).subscribe({
        next: () => {
          // Cerrar el modal simulando un click en el botón de cierre
          document.getElementById('closePasswordModal')?.click();
          this.passwordForm.reset();
          this.passwordStep = 1;
          Swal.fire({
            title: '¡Contraseña actualizada!',
            text: 'Su conntraseña ha sido actualizada con éxito',
            icon: 'success',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        },
        error: () => {
          Swal.fire({
            title: 'Error',
            text: 'No se pudo actualizar la contraseña. Verifique los datos.',
            icon: 'error',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: 'var(--color-acento)'
          });
        }
      });
    }
  }
}
