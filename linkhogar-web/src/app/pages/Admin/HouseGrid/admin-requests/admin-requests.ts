import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {AdminService} from '../../../../services/admin/admin-service';
import {HouseService} from '../../../../services/house/house-service'; // Añadido
import {PageResponse} from '../../../../Models/Shared/PageResponse';
import {HouseReport} from '../../../../Models/Houses/HouseReport'; // Asegúrate de tener este modelo
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
  private houseService = inject(HouseService); // Para obtener las denuncias

  reports = signal<PageResponse<HouseReport> | null>(null);
  isLoading = signal(false);
  currentPage = signal(0);
  readonly pageSize = 10;

  // Señal para guardar la denuncia seleccionada y mostrarla en el modal
  selectedReport = signal<HouseReport | null>(null);

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.isLoading.set(true);
    // Usamos el endpoint de paginación de denuncias
    this.houseService.getAllReports(this.currentPage(), this.pageSize).subscribe({
      next: (data) => {
        this.reports.set(data);
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
    this.loadReports();
  }

  getPages(): number[] {
    const total = this.reports()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }

  // Abre el modal y carga los datos de la denuncia
  viewReportDetails(report: HouseReport) {
    this.selectedReport.set(report);
  }

  // Toma la decisión sobre qué hacer con la publicación
  decideOnPublication(report: HouseReport) {
    Swal.fire({
      title: 'Decisión sobre la publicación',
      text: '¿Qué deseas hacer con el anuncio denunciado?',
      icon: 'warning',
      showDenyButton: true,
      showCancelButton: true,
      confirmButtonText: 'Eliminar Anuncio',
      denyButtonText: 'Ignorar Denuncia',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#dc3545', // Rojo para eliminar
      denyButtonColor: '#6c757d', // Gris para ignorar
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteHouse(report);
      } else if (result.isDenied) {
        this.removeReportFromList(report.id);
        Swal.fire('Ignorado', 'La denuncia ha sido descartada.', 'info');
      }
    });
  }

  // Actualiza la señal de la lista tras tomar una decisión
  private removeReportFromList(reportId: string) {
    this.reports.update(page => {
      if (!page) return page;
      return {
        ...page,
        content: page.content.filter(r => r.id !== reportId),
        totalElements: page.totalElements - 1
      };
    });
  }

  get reportsList() { return this.reports()?.content ?? []; }
  get totalPages() { return this.reports()?.totalPages ?? 0; }
  get isFirstPage() { return this.reports()?.first ?? true; }
  get isLastPage() { return this.reports()?.last ?? true; }
  get totalElements() { return this.reports()?.totalElements ?? 0; }


  deleteHouse(report: HouseReport) {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Al eliminar este anuncio quedará archivado y no tendrá visibilidad',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Eliminar Anuncio',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#dc3545', // Rojo para eliminar
      denyButtonColor: '#6c757d', // Gris para ignorar
    }).then((result)=> {
      if (result.isConfirmed) {
        this.adminService.deleteHouse(report.houseId).subscribe({
          next: () => {
            this.removeReportFromList(report.id);
            this.houseService.deleteReport(report.id);
            Swal.fire('Eliminado', 'La publicación ha sido retirada.', 'success');
          },
          error: () =>{
            Swal.fire('Error', 'No se pudo retirar la publicación.', 'error')
          }
        })
      }
    })
  }
}
