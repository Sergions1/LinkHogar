import { Component, effect, inject, signal } from '@angular/core';
import { HouseCardResponse } from '../../../Models/Houses/house-card-response.interface';
import { PageResponse } from '../../../Models/Shared/PageResponse';
import { AuthService } from '../../../services/auth/auth.service';
import { HouseService } from '../../../services/house/house-service';
import Swal from 'sweetalert2';
import { HouseCard } from '../../shared/house-card/house-card';
import { EntityCardView } from '../../shared/Grids/entity-card-view/entity-card-view';
import { UserResponse } from '../../../Models/Users/UserResponse';
import {Router} from '@angular/router';
import {ManageMembersModal} from './manage-members-modal/manage-members-modal';

@Component({
  selector: 'app-my-publications',
  imports: [HouseCard, EntityCardView, ManageMembersModal],
  templateUrl: './my-publications.html',
  styleUrl: './my-publications.scss',
})
export class MyPublications {
  private authService = inject(AuthService);
  private houseService = inject(HouseService); // Ojo, usamos HouseService ahora
  private router = inject(Router);

  houses = signal<PageResponse<HouseCardResponse> | null>(null);
  isLoading = signal(false);
  currentUser: UserResponse | null = null;

  selectedHouseId = signal<string | null>(null);

  constructor() {
    effect(() => {
      const user = this.authService.currentUser();
      if (user) {
        this.currentUser = user;
        this.loadPublications(0);
      }
    });
  }

  loadPublications(page: number) {
    if (!this.currentUser) return;

    this.isLoading.set(true);
    this.houseService.getHousesByOwner(this.currentUser.id, page).subscribe({
      next: (response) => {
        this.houses.set(response);
        this.isLoading.set(false);
      },
      error: (err) => {
        Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar tus publicaciones.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        });
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(newPage: number) {
    this.loadPublications(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onDeletePublication(houseId: string) {
    // Lo ideal es confirmar con el usuario antes de borrar
    Swal.fire({
      title: '¿Estás seguro?',
      text: "Esta acción no se puede deshacer y borrará la publicación.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33', // Rojo
      cancelButtonColor: 'var(--color-primario)',
      confirmButtonText: 'Sí, borrar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.houseService.deleteHouse(houseId).subscribe({
          next:() => {
            Swal.fire({
              title: 'Eliminado',
              text: 'Publicación eliminada correctamente.',
              icon: 'success',
              confirmButtonText: 'Aceptar',
              confirmButtonColor: 'var(--color-acento)'
            });
            this.loadPublications(0);
          },
          error: (err) => {
            // ERROR: Mostramos mensaje rojo
            Swal.fire({
              title: 'Error',
              text: 'No se pudo eliminar la publicación. Inténtalo de nuevo.',
              icon: 'error',
              confirmButtonText: 'Aceptar',
              confirmButtonColor: 'var(--color-acento)'
            });
          }
        })
      }
    });
  }

  onEditPublication(houseId: string) {
    this.router.navigate(['/editar', houseId]);
  }

  onManageMembers(houseId: string) {
    this.selectedHouseId.set(houseId);
  }

  closeMembersModal() {
    this.selectedHouseId.set(null);
  }
}
