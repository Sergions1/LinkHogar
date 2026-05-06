import {Component, EventEmitter, inject, Input, OnInit, Output, signal} from '@angular/core';
import {HouseService} from '../../../../services/house/house-service';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import Swal from 'sweetalert2';
import {HomeService} from '../../../../services/home/home-service';

@Component({
  selector: 'app-manage-members-modal',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './manage-members-modal.html',
  styleUrl: './manage-members-modal.scss',
})
export class ManageMembersModal implements OnInit {
  @Input({ required: true }) homeId!: string;
  @Output() close = new EventEmitter<void>();

  private houseService = inject(HouseService);
  private homeService = inject(HomeService);
  private fb = inject(FormBuilder);

  members = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);

  addForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit() {
    this.loadMembers();
  }

  loadMembers() {
    this.isLoading.set(true);
    this.homeService.getHomeMembers(this.homeId).subscribe({
      next: (data) => {
        this.members.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        Swal.fire('Error', 'No se pudieron cargar los miembros', 'error');
      }
    });
  }

  addMember() {
    if (this.addForm.invalid || this.isSubmitting()) return;

    this.isSubmitting.set(true);
    const email = this.addForm.value.email;

    this.homeService.addMemberToHome(this.homeId, email).subscribe({
      next: () => {
        this.addForm.reset();
        this.isSubmitting.set(false);
        this.loadMembers(); // Recargar la lista
        Swal.fire({
          title: 'Añadido',
          text: 'Usuario añadido al hogar correctamente.',
          icon: 'success',
          confirmButtonColor: 'var(--color-acento)'
        });
      },
      error: (err) => {
        this.isSubmitting.set(false);
        // Aquí mostraremos el error que nos envíe el backend (ej. "Ya tiene hogar")
        const errorMsg = err.error?.message || 'Error al añadir al usuario. Comprueba el correo.';
        Swal.fire({
          title: 'Error',
          text: errorMsg,
          icon: 'error',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }

  removeMember(memberId: string) {
    Swal.fire({
      title: '¿Eliminar del hogar?',
      text: 'Este usuario perderá el acceso a las tareas y gastos.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: 'var(--color-acento)',
      confirmButtonText: 'Eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.homeService.removeMemberFromHome(this.homeId, memberId).subscribe({
          next: () => {
            this.loadMembers();
          },
          error: () => {
            Swal.fire('Error', 'No se pudo eliminar al usuario', 'error');
          }
        });
      }
    });
  }
}
