// admin-requests.component.ts
import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {AdminService} from '../../../../services/admin/admin-service';
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {HouseResponse} from '../../../../Models/Houses/HouseResponse';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-admin-requests',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-requests.html',
  styleUrls: ['./admin-requests.scss']
})
export class AdminRequests implements OnInit {
  private adminService = inject(AdminService);

  requests = signal<PageResponse<HouseResponse> | null>(null);
  isLoading = signal(false);
  currentPage = signal(0);
  readonly pageSize = 10;

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.isLoading.set(true);
    this.adminService.getPendingHouses(this.currentPage(), this.pageSize).subscribe({
      next: (data) => {
        this.requests.set(data);
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
    this.loadRequests();
  }

  getPages(): number[] {
    const total = this.requests()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }

  handleRequest(id: string, accept: boolean) {
    const actionText = accept ? 'aprobar' : 'rechazar';
    const newStatus = accept ? 'PUBLISHED' : 'DRAFT'; // DRAFT para que el usuario pueda corregirlo, o usa ARCHIVED

    Swal.fire({
      title: `¿${accept ? 'Aprobar' : 'Rechazar'} solicitud?`,
      text: `Vas a ${actionText} esta publicación.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: `Sí, ${actionText}`,
      cancelButtonText: 'Cancelar',
      confirmButtonColor: accept ? '#198754' : '#dc3545',
      cancelButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.setHouseStatus(id, newStatus).subscribe({
          next: () => {
            this.requests.update(page => {
              if (!page) return page;
              return {
                ...page,
                content: page.content.filter(h => h.id !== id),
                totalElements: page.totalElements - 1
              };
            });
            Swal.fire({
              title: '¡Éxito!',
              text: `La solicitud ha sido ${accept ? 'aprobada' : 'rechazada'}.`,
              icon: 'success',
              confirmButtonColor: 'var(--color-acento)',
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Error:', err);
            Swal.fire({
              title: 'Error',
              text: 'No se pudo procesar la solicitud.',
              icon: 'error',
              confirmButtonColor: 'var(--color-acento)'
            });
          }
        });
      }
    });
  }

  get requestsList() { return this.requests()?.content ?? []; }
  get totalPages() { return this.requests()?.totalPages ?? 0; }
  get isFirstPage() { return this.requests()?.first ?? true; }
  get isLastPage() { return this.requests()?.last ?? true; }
  get totalElements() { return this.requests()?.totalElements ?? 0; }
}
