import {Component, EventEmitter, inject, Input, OnInit, Output, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {GenderPipe} from '../../../../pipes/GenderPipe';
import {OccupationPipe} from '../../../../pipes/OccupationPipe';
import {HouseCardResponse} from '../../../../Models/Houses/house-card-response.interface';
import {HouseService} from '../../../../services/house/house-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manage-rooms-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, GenderPipe, OccupationPipe],
  templateUrl: './manage-rooms-modal.html',
  styleUrl: './manage-rooms-modal.scss'
})
export class ManageRoomsModal implements OnInit {
  @Input({ required: true }) house!: HouseCardResponse;
  @Output() close = new EventEmitter<void>();

  private houseService = inject(HouseService);
  activeRoomIndex = signal(0);
  isSaving = signal(false);

  ngOnInit() {
    // Asegurarnos de que las habitaciones sin inquilino tengan el objeto inicializado
    // para que el ngModel del HTML no falle al intentar acceder a room.currentTenant.gender
    this.house.roomList.forEach(room => {
      if (!room.currentTenant) {
        room.currentTenant = {
          gender: null,
          ageRange: null,
          occupation: null,
          description: null,
          isSmoker: null,
          hasPets: null
        };
      }
    });
  }

  setActiveRoom(index: number) {
    this.activeRoomIndex.set(index);
  }

  onClose() {
    this.close.emit();
  }

  saveTenantInfo() {
    const room = this.house.roomList[this.activeRoomIndex()];
    this.isSaving.set(true);

    // Si la cambian a disponible, limpiamos los datos del inquilino antes de enviar
    const tenantData = room.status === 'AVAILABLE' ? null : room.currentTenant;

    // Aquí llamaremos al endpoint de Java
    this.houseService.updateRoomTenant(this.house.id, room.id, tenantData, room.status).subscribe({
      next: () => {
        this.isSaving.set(false);
        Swal.fire({
          title: 'Guardado',
          text: 'Información actualizada correctamente',
          icon: 'success',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        });

      },
      error: () => {
        this.isSaving.set(false);
        Swal.fire({
          title: 'Error',
          text: 'No se pudo actualizar la información',
          icon: 'error',
          confirmButtonText: 'Aceptar',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }
}
