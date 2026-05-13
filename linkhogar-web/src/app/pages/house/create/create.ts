// create.ts
import {Component, signal, computed, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { TypeStep } from './steps/type-step/type-step';
import { FeaturesStep, FeaturesData } from './steps/features-step/features-step';
import { PriceStep } from './steps/price-step/price-step';
import { PhotosStep } from './steps/photos-step/photos-step';
import { ReviewStep } from './steps/review-step/review-step';
import {UbicationData, UbicationStep} from './steps/ubication-step/ubication-step';
import {  AnnouncementDetailData,  AnnouncementDetailStep} from './steps/announcement-detail-step/announcement-detail-step';
import {HouseService} from '../../../services/house/house-service';
import {ActivatedRoute, Router} from '@angular/router';
import Swal from 'sweetalert2';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {RoomsStep} from './steps/room-step/room-step';
import {forkJoin, Observable} from 'rxjs';

export interface RoomDetail {
  id? :string;
  name: string;
  description: string;
  price: number | null;
  size: number | null;
  bedType: string;
  hasPrivateBath: boolean;
  photos: File[];
  existingPhotosUrls?: string[];
}

export interface HouseForm {
  location: UbicationData;
  type: string;
  features: FeaturesData;
  rentalMode: 'COMPLETE' | 'BY_ROOM';
  roomList: RoomDetail[];
  price: number | null;
  photos: File[];
  details: AnnouncementDetailData;
}

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [
    CommonModule,
    UbicationStep,
    TypeStep,
    FeaturesStep,
    RoomsStep,
    PriceStep,
    PhotosStep,
    AnnouncementDetailStep,
    ReviewStep
  ],
  templateUrl: './create.html',
  styleUrl: './create.scss'
})
export class Create implements OnInit{
  private houseService = inject(HouseService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly allSteps = [
    { id: 'location', label: 'Ubicación',   icon: 'bi-geo-alt' },
    { id: 'type',     label: 'Tipo',        icon: 'bi-house' },
    { id: 'features', label: 'Características', icon: 'bi-list-check' },
    { id: 'rooms',    label: 'Habitaciones',icon: 'bi-door-closed' }, // Paso dinámico
    { id: 'price',    label: 'Precio',      icon: 'bi-tag' },
    { id: 'photos',   label: 'Fotos',       icon: 'bi-images' },
    { id: 'details',  label: 'Descripción', icon: 'bi-comments' },
    { id: 'review',   label: 'Revisión',    icon: 'bi-check-circle' },
  ];

  currentStep = signal(0);
  stepValid = signal(false);
  isLoading = signal(false);

  isEditMode = signal(false);
  editHouseId = signal<string | null>(null);
  existingPhotosUrls = signal<string[]>([]);

  formData = signal<HouseForm>({
    location: {
      street: '',
      number: '',
      city: '',
      province: '',
      cp: '',
      floor: '',
      door: ''
    },
    type: '',
    features: {
      size: null, rooms: null, baths: null,
      lift: false, furnished: false, airConditioned: false,
      terrace: false, balcony: false, garage: false,
      pool: false, petsAllowed: false, storage: false, commonAreas: false
    },
    rentalMode: 'COMPLETE',
    roomList: [],
    price: null,
    photos: [],
    details:{
      title: '',
      description: '',
    }
  });

  visibleSteps = computed(() => {
    if (this.formData().rentalMode === 'BY_ROOM') {
      return this.allSteps;
    }
    return this.allSteps.filter(s => s.id !== 'rooms');
  });

  progress = computed(() => Math.round((this.currentStep() / (this.visibleSteps().length - 1)) * 100));
  isFirst = computed(() => this.currentStep() === 0);
  isLast  = computed(() => this.currentStep() === this.visibleSteps().length - 1);

  calculatedMinRoomPrice = computed(() => {
    const rooms = this.formData().roomList;
    if (rooms.length === 0) return 0;
    const validPrices = rooms.map(r => r.price).filter((p): p is number => p !== null && p > 0);
    return validPrices.length > 0 ? Math.min(...validPrices) : 0;
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.editHouseId.set(id);
      this.loadHouseData(id);
    } else {
      // Si estamos creando, el paso 0 requiere validación (que rellenemos la dirección)
      // Si no pones esto, podrías darle a 'next' estando el formulario vacío
      this.stepValid.set(false);
    }
  }

  next() {
    if (!this.isLast()) {
      // Si estamos creando, bloqueamos el siguiente paso hasta que lo rellene.
      // Si estamos editando, asumimos que ya es válido (a menos que el hijo diga lo contrario).
      if (!this.isEditMode()) {
        this.stepValid.set(false);
      } else {
        this.stepValid.set(true);
      }
      this.currentStep.update(s => s + 1);
    }
  }

  prev() {
    if (!this.isFirst()) {
      this.stepValid.set(true); // pasos anteriores ya fueron validados
      this.currentStep.update(s => s - 1);
    }
  }

  onStepValidChange(valid: boolean) {
    this.stepValid.set(valid);
  }

  onLocationChange(location: UbicationData) {
    this.formData.update(d => ({ ...d, location }));
  }

  onTypeChange(type: string) {
    this.formData.update(d => ({ ...d, type }));
  }

  onFeaturesChange(features: FeaturesData) {
    this.formData.update(d => ({ ...d, features }));
  }

  onRentalModeChange(rentalMode: 'COMPLETE' | 'BY_ROOM') {
    this.formData.update(d => ({ ...d, rentalMode }));
  }

  // 👇 Nuevo handler para las habitaciones
  onRoomsChange(roomList: RoomDetail[]) {
    this.formData.update(d => ({ ...d, roomList }));
  }

  onPriceChange(price: number | null) {
    this.formData.update(d => ({ ...d, price }));
  }

  onPhotosChange(photos: File[]) {
    this.formData.update(d => ({ ...d, photos }));
  }

  onDetailsChange(details: AnnouncementDetailData) {
    this.formData.update(d => ({ ...d, details }));
  }

  showCreatingError(){
    Swal.fire({
      title: 'Error al publicar',
      text: 'No ha sido posible publicar el anuncio',
      icon: 'error',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: 'var(--color-primario)'
    })
  }

  submit() {
    if (this.isEditMode()) {
      this.submitEdit(); // --- NUEVO: Desviamos a edición ---
    } else {
      this.submitCreate(); // Tu lógica de creación anterior
    }
  }

  submitCreate() {
    this.isLoading.set(true);

    this.houseService.createHouse(this.formData()).subscribe({
      next: (response: any) => {
        // response ahora tiene { id: '...', rooms: { 'Habitación 1': '...', 'Habitación 2': '...' } }
        const houseId = response.id;
        const roomIdsMap = response.rooms || {};
        const tituloLimpio = this.formData().details.title.toLowerCase().replace(/\s+/g, '-');

        // Aquí guardaremos todas las peticiones de subida de fotos que hay que hacer
        const uploadObservables: Observable<any>[] = [];

        // 1. Añadimos la subida de fotos de la casa (si hay)
        const housePhotos = this.formData().photos;
        if (housePhotos && housePhotos.length > 0) {
          uploadObservables.push(this.houseService.uploadHouseImages(houseId, housePhotos));
        }

        // 2. Añadimos la subida de fotos de cada habitación (si hay)
        if (this.formData().rentalMode === 'BY_ROOM') {
          this.formData().roomList.forEach(room => {
            if (room.photos && room.photos.length > 0) {
              const roomId = roomIdsMap[room.name]; // Buscamos su ID por el nombre
              if (roomId) {
                uploadObservables.push(this.houseService.uploadRoomImages(houseId, roomId, room.photos));
              }
            }
          });
        }

        // 3. Ejecutamos todas las subidas a la vez
        if (uploadObservables.length > 0) {
          forkJoin(uploadObservables).subscribe({
            next: () => {
              this.isLoading.set(false);
              this.showSuccessAndNavigate(tituloLimpio, houseId);
            },
            error: (err) => {
              console.log('Error subiendo alguna foto:', err);
              this.isLoading.set(false);
              this.showWarningAndNavigate(tituloLimpio, houseId);
            }
          });
        } else {
          // Si no había NINGUNA foto que subir, terminamos directamente
          this.isLoading.set(false);
          this.showSuccessAndNavigate(tituloLimpio, houseId);
        }
      },
      error: (err) => {
        console.log('Error creando la casa:', err);
        this.isLoading.set(false);
        this.showCreatingError();
      }
    });
  }

  // --- Métodos de ayuda para las alertas ---

  private showSuccessAndNavigate(tituloLimpio: string, id: string) {
    Swal.fire({
      title: '¡Anuncio publicado!',
      text: 'Su anuncio ha sido publicado con éxito',
      icon: 'success',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: 'var(--color-primario)'
    }).then(() => {
      this.router.navigate(['/inmueble', tituloLimpio, id]);
    });
  }

  private showWarningAndNavigate(tituloLimpio: string, id: string) {
    Swal.fire({
      title: '¡Atención!',
      text: 'Su anuncio ha sido publicado, pero hubo un error subiendo algunas de las imágenes.',
      icon: 'warning',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: 'var(--color-primario)'
    }).then(() => {
      this.router.navigate(['/inmueble', tituloLimpio, id]);
    });
  }


  loadHouseData(id: string) {
    this.isLoading.set(true);

    this.houseService.getHouseById(id).subscribe({
      next: (house: HouseResponse) => {
        this.formData.set({
          location: {
            street: house.address.street,
            number: house.address.number?.toString() || "Bajo",
            city: house.address.city,
            province: house.address.province,
            cp: house.address.cp?.toString() || "",
            floor: house.address.floor || '',
            door: house.address.door || '',
            latitude: house.address.latitude,
            longitude: house.address.longitude
          },
          type: house.houseType,
          features: {
            size: house.size, rooms: house.rooms, baths: house.baths,
            lift: house.lift, furnished: house.furnished, airConditioned: house.airConditioned,
            terrace: house.terrace, balcony: house.balcony, garage: house.garage,
            pool: house.pool, petsAllowed: house.petsAllowed, storage: house.storage,
            commonAreas: house.commonAreas
          },
          // 👇 Mapeo del modo y las habitaciones
          rentalMode: house.rentalMode === 'BY_ROOM' ? 'BY_ROOM' : 'COMPLETE',
          roomList: house.roomList ? house.roomList.map((r: any) => ({
            id: r.id,
            name: r.name,
            description: r.description || '', // Asumiendo que añades descripción
            price: r.price,
            size: r.size,
            bedType: r.bedType,
            hasPrivateBath: r.hasPrivateBath,
            photos: [], // Las fotos existentes de habitaciones habría que traerlas del backend si aplica
            existingPhotosUrls: r.images || r.photoUrls || []
          })) : [],
          price: house.price,
          photos: [],
          details: {
            title: house.title,
            description: house.description
          }
        });

        this.existingPhotosUrls.set(house.images || []);
        this.stepValid.set(true);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        Swal.fire('Error', 'No se pudieron cargar los datos', 'error');
        this.router.navigate(['/mis-publicaciones']);
      }
    });
  }

  submitEdit() {
    this.isLoading.set(true);
    const id = this.editHouseId();
    if (!id) return;

    // 1. Actualizamos el texto primero
    this.houseService.updateHouse(id, this.formData()).subscribe({
      next: (response: any) => {
        const tituloLimpio = this.formData().details.title.toLowerCase().replace(/\s+/g, '-');

        // Extraemos los IDs de las habitaciones (Igual que en la creación)
        const roomIdsMap = response?.rooms || {};

        // Preparamos el array de subidas
        const uploadObservables: Observable<any>[] = [];

        // A. Fotos nuevas de la casa
        const housePhotos = this.formData().photos;
        if (housePhotos && housePhotos.length > 0) {
          uploadObservables.push(this.houseService.uploadHouseImages(id, housePhotos));
        }

        // B. Fotos nuevas de las habitaciones
        if (this.formData().rentalMode === 'BY_ROOM') {
          this.formData().roomList.forEach(room => {
            if (room.photos && room.photos.length > 0) {
              const roomId = roomIdsMap[room.name];
              if (roomId) {
                uploadObservables.push(this.houseService.uploadRoomImages(id, roomId, room.photos));
              } else {
                console.warn(`No se encontró ID para la habitación ${room.name} en la respuesta del servidor.`);
              }
            }
          });
        }

        // 2. Si hay fotos que subir (de casa o habitaciones), las lanzamos en paralelo
        if (uploadObservables.length > 0) {
          forkJoin(uploadObservables).subscribe({
            next: () => {
              this.finishSubmit(tituloLimpio, id, true);
            },
            error: (err) => {
              console.error('Error subiendo imágenes en edición:', err);
              this.finishSubmit(tituloLimpio, id, false, true);
            }
          });
        } else {
          // Si no hay fotos nuevas, terminamos
          this.finishSubmit(tituloLimpio, id, true);
        }
      },
      error: (err) => {
        console.error(err);
        this.isLoading.set(false);
        Swal.fire('Error', 'No se ha podido actualizar el anuncio', 'error');
      }
    });
  }

  private finishSubmit(tituloLimpio: string, id: string, isSuccess: boolean, isImageError: boolean = false) {
    this.isLoading.set(false);

    if (isImageError) {
      Swal.fire('Atención!', 'Anuncio actualizado con éxito, pero hubo un error subiendo las nuevas imágenes', 'warning')
        .then(() => this.router.navigate(['/inmueble', tituloLimpio, id]));
    } else {
      Swal.fire('¡Éxito!', `Su anuncio ha sido ${this.isEditMode() ? 'actualizado' : 'publicado'} con éxito`, 'success')
        .then(() => this.router.navigate(['/inmueble', tituloLimpio, id]));
    }
  }

  deleteOldPhoto(imageUrl: string) {
    const id = this.editHouseId();
    if (!id) return;

    Swal.fire({
      title: '¿Borrar imagen?',
      text: "Se eliminará permanentemente de tu anuncio.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: 'var(--color-primario)',
      confirmButtonText: 'Sí, borrar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        const backupUrls = [...this.existingPhotosUrls()];

        this.existingPhotosUrls.set(this.existingPhotosUrls().filter(url => url !== imageUrl));
        this.houseService.deleteHouseImage(id, imageUrl).subscribe({
          next: () => {},
          error: (err) => {
            console.error(err);
            // 4. RESTAURAR SI FALLA
            this.existingPhotosUrls.set(backupUrls);
            Swal.fire('Error', 'No se pudo borrar la imagen en el servidor. Se ha restaurado.', 'error');
          }
        });
      }
    });
  }

  deleteOldRoomPhoto(roomIndex: number, imageUrl: string) {
    const houseId = this.editHouseId();
    const room = this.formData().roomList[roomIndex];

    if (!houseId || !room.id) return;

    Swal.fire({
      title: '¿Borrar imagen de la habitación?',
      text: "Se eliminará permanentemente.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, borrar',
      confirmButtonColor: '#d33',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        const backupRoomList = [...this.formData().roomList];

        this.formData.update(old => {
          const newList = [...old.roomList];
          newList[roomIndex] = {
            ...newList[roomIndex],
            existingPhotosUrls: newList[roomIndex].existingPhotosUrls?.filter(u => u !== imageUrl)
          };
          return { ...old, roomList: newList };
        });

        this.houseService.deleteRoomImage(houseId, room.id!, imageUrl).subscribe({
          next: () => {},
          error: (err) => {
            console.error(err);
            this.formData.update(old => ({ ...old, roomList: backupRoomList }));
            Swal.fire('Error', 'No se pudo borrar la imagen en el servidor. Se ha restaurado.', 'error');
          }
        });
      }
    });
  }

}
