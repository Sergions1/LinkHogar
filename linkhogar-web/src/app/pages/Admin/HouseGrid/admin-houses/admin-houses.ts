// admin/casas/admin-casas.component.ts
import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HouseService} from '../../../../services/house/house-service';
import {AdminService} from '../../../../services/admin/admin-service';
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {HouseResponse} from '../../../../Models/Houses/HouseResponse';
import Swal from 'sweetalert2';
import {FormsModule} from '@angular/forms';
import {RouterModule, Router} from '@angular/router';


@Component({
  selector: 'app-admin-houses',
  standalone: true,
  imports: [CommonModule,RouterModule, FormsModule],
  templateUrl: './admin-houses.html',
  styleUrls: ['./admin-houses.scss']
})
export class AdminHousesComponent implements OnInit {
  private router = inject(Router);
  private houseService = inject(HouseService);
  public adminService = inject(AdminService);


  houses = this.adminService.adminHouses;
  isLoading = signal(false);
  currentPage = signal(0);
  readonly pageSize = 10;

  // Variables para el Modal de Estado
  showStatusModal = false;
  isSubmittingStatus = false;
  selectedHouseId: string | null = null;
  selectedStatus: string = '';

  readonly availableStatuses = [
    'DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'PAUSED', 'EXPIRED', 'ARCHIVED'
  ];

  ngOnInit() {
    // Mandamos a precargar toda la sección administrativa en paralelo de fondo


    // Si los anuncios de administración no se han cargado nunca, hacemos la primera llamada
    if (!this.houses()) {
      this.loadHouses();
    }
  }

  loadHouses() {
    this.isLoading.set(true);
    this.houseService.getPaginatedHouses(this.currentPage(), this.pageSize).subscribe({
      next: (data) => {
        this.houses.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.isLoading.set(false);
      }
    });
  }

  goToPage(page: number) {
    this.currentPage.set(page);
    this.loadHouses();
  }

  getPages(): number[] {
    const total = this.houses()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }

  deleteHouse(id: string) {
    Swal.fire({
      title: '¿Eliminar anuncio?',
      text: 'Esta acción eliminará la publicación de forma permanente.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#FF0000',
      cancelButtonColor: 'var(--color-acento)'
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.deleteHouse(id).subscribe({
          next: () => {
            this.houses.update(page => {
              if (!page) return page;
              return {
                ...page,
                content: page.content.filter(h => h.id !== id),
                totalElements: page.totalElements - 1
              };
            });
            Swal.fire({
              title: '¡Eliminado!',
              text: 'El anuncio ha sido eliminado correctamente.',
              icon: 'success',
              confirmButtonColor: 'var(--color-acento)',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Error al eliminar:', err);
            Swal.fire({
              title: 'Error',
              text: 'No se pudo eliminar el anuncio.',
              icon: 'error',
              confirmButtonColor: 'var(--color-acento)'
            });
          }
        });
      }
    });
  }

  editHouse(id: string) {
    this.router.navigate(['/editar', id], {
      queryParams: { fromAdmin: true }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PUBLISHED': return 'bg-success';
      case 'PAUSED': return 'bg-warning text-dark';
      case 'DELETED': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PUBLISHED': return 'Publicado';
      case 'PAUSED': return 'Pausado';
      case 'ARCHIVED': return 'Archivado';
      case 'DRAFT': return 'En edición';
      case 'PENDING_REVIEW': return 'Pendiente de revisión';
      case 'EXPIRED': return 'Expirado';
      default: return status;
    }
  }

  parseStatusLabel(label: string): string {
    switch (label) {
      case 'Publicado': return 'PUBLISHED';
      case 'Pausado': return 'PAUSED';
      case 'Archivado': return 'ARCHIVED';
      case 'En edición': return 'DRAFT';
      case 'Pendiente de revisión': return 'PENDING_REVIEW';
      case 'Expirado': return 'EXPIRED';
      default: return label;
    }
  }

  get housesList() {
    return this.houses()?.content ?? [];
  }

  get totalPages() {
    return this.houses()?.totalPages ?? 0;
  }

  get isFirstPage() {
    return this.houses()?.first ?? true;
  }

  get isLastPage() {
    return this.houses()?.last ?? true;
  }

  get totalElements() {
    return this.houses()?.totalElements ?? 0;
  }

  toggleStatus(id: string, currentStatus: string) {
    this.selectedHouseId = id;
    this.selectedStatus = currentStatus;
    this.showStatusModal = true;
  }

  closeStatusModal() {
    this.showStatusModal = false;
    this.selectedHouseId = null;
    this.selectedStatus = '';
  }

  submitStatusChange() {
    if (!this.selectedHouseId || !this.selectedStatus) return;

    const newStatus = this.parseStatusLabel(this.selectedStatus);
    this.isSubmittingStatus = true;
    this.adminService.setHouseStatus(this.selectedHouseId, this.parseStatusLabel(this.selectedStatus)).subscribe({
      next: () => {
        this.isSubmittingStatus = false;
        this.houses.update(page => {
          if (!page) return page;
          return {
            ...page,
            content: page.content.map(h =>
              h.id === this.selectedHouseId ? { ...h, publicationStatus: newStatus } : h
            )
          };
        });
        this.closeStatusModal();
        Swal.fire({
          title: '¡Estado actualizado!',
          icon: 'success',
          confirmButtonColor: 'var(--color-acento)',
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: (err) => {
        console.error(err);
        this.isSubmittingStatus = false;
        Swal.fire({
          title: 'Error',
          text: 'No se pudo actualizar el estado.',
          icon: 'error',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }

  redirectToRequests(){
    this.router.navigate(['/admin/houses/requests']);
  }
}
