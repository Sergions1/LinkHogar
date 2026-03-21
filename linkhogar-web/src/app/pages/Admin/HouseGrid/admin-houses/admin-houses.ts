// admin/casas/admin-casas.component.ts
import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {HouseService} from '../../../../services/house/house-service';
import {AdminService} from '../../../../services/admin/admin-service';
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {HouseResponse} from '../../../../Models/Houses/HouseResponse';


@Component({
  selector: 'app-admin-houses',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-houses.html',
  styleUrls: ['./admin-houses.scss']
})
export class AdminHousesComponent implements OnInit {
  private houseService = inject(HouseService);
  private adminService = inject(AdminService);

  houses = signal<PageResponse<HouseResponse> | null>(null);
  isLoading = signal(false);
  currentPage = signal(0);
  readonly pageSize = 10;

  ngOnInit() {
    this.loadHouses();
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
    // TODO: conectar con handler de borrado
    console.log('Eliminar casa:', id);
  }

  toggleStatus(id: string, currentStatus: string) {
    // TODO: conectar con handler de cambio de estado
    console.log('Cambiar estado:', id, currentStatus);
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
      case 'DELETED': return 'Eliminado';
      default: return status;
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
}
