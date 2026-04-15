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

export interface HouseForm {
  location: UbicationData;
  type: string;
  features: FeaturesData;
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

  readonly steps = [
    { label: 'Location',    icon: 'bi-geo-alt' },
    { label: 'Type',        icon: 'bi-house' },
    { label: 'Features',    icon: 'bi-list-check' },
    { label: 'Price',       icon: 'bi-tag' },
    { label: 'Photos',      icon: 'bi-images' },
    { label: 'Desripción',  icon: 'bi-comments' },
    { label: 'Revisión',      icon: 'bi-check-circle' },
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
    price: null,
    photos: [],
    details:{
      title: '',
      description: '',
    }
  });

  progress = computed(() =>
    Math.round(((this.currentStep()) / 6) * 100)
  );

  isFirst = computed(() => this.currentStep() === 0);
  isLast  = computed(() => this.currentStep() === this.steps.length - 1);

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
    console.log('Publishing:', this.formData());

    this.isLoading.set(true);
    this.houseService.createHouse(this.formData()).subscribe({
      next: (response: any) =>{
        const tituloLimpio = this.formData().details.title
          .toLowerCase()
          .replace(/\s+/g, '-');
        const photos = this.formData().photos;

        if (photos && photos.length > 0){
          this.houseService.uploadHouseImages(response.id, photos).subscribe({
            next: () => {
              this.isLoading.set(false);
              Swal.fire({
                title: '¡Anuncio publicado!',
                text: 'Su anuncio ha sido publicado con éxito',
                icon: 'success',
                confirmButtonText: 'Aceptar',
                confirmButtonColor: 'var(--color-primario)'
              }).then((result) =>{
                this.router.navigate(['/inmueble', tituloLimpio, response.id]);
              })
            },
            error: (imgErr: any) => {
              console.log(imgErr);
              Swal.fire({
                title: 'Atención!',
                text: 'Su anuncio ha sido publicado con éxito, pero hubo un error con las imagenes',
                icon: 'warning',
                confirmButtonText: 'Aceptar',
                confirmButtonColor: 'var(--color-primario)'
              }).then((result) =>{
                this.router.navigate(['/inmueble', tituloLimpio, response.id]);
              })
            }
          })
        }

      },
      error: (err) =>{
        console.log(err);
        this.isLoading.set(false);
        this.showCreatingError();
      }
    })
  }


  loadHouseData(id: string) {
    this.isLoading.set(true);

    this.houseService.getHouseById(id).subscribe({
      next: (house: HouseResponse) => { // Idealmente cambia 'any' por tu modelo HouseResponse

        // Transformamos la respuesta del backend al formato del HouseForm
        this.formData.set({
          location: {
            street: house.address.street,
            number: house.address.number?.toString() || "Bajo", // Asigna los campos exactos
            city: house.address.city,
            province: house.address.province,
            cp: house.address.cp?.toString() || "",
            floor: house.address.floor || '',
            door: house.address.door || '',
            latitude: house.address.latitude,
            longitude: house.address.longitude
          },
          type: house.houseType, // Ajusta según tu DTO
          features: {
            size: house.size, rooms: house.rooms, baths: house.baths,
            lift: house.lift, furnished: house.furnished, airConditioned: house.airConditioned,
            terrace: house.terrace, balcony: house.balcony, garage: house.garage,
            pool: house.pool, petsAllowed: house.petsAllowed, storage: house.storage,
            commonAreas: house.commonAreas
          },
          price: house.price,
          photos: [],
          details: {
            title: house.title,
            description: house.description
          }
        });

        // Guardamos las URLs de las fotos antiguas separadas para pasárselas al PhotosStep
        this.existingPhotosUrls.set(house.images || []);

        // Como ya tiene datos cargados de la base de datos, el primer paso es válido
        this.stepValid.set(true);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        Swal.fire('Error', 'No se pudieron cargar los datos de la vivienda', 'error');
        this.router.navigate(['/mis-publicaciones']);
      }
    });
  }

  submitEdit() {
    this.isLoading.set(true);
    const id = this.editHouseId();
    if(!id) return;

    // 1. Actualizamos el texto primero
    this.houseService.updateHouse(id, this.formData()).subscribe({
      next: () => {
        const photos = this.formData().photos; // Son las NUEVAS fotos (objetos File)
        const tituloLimpio = this.formData().details.title.toLowerCase().replace(/\s+/g, '-');

        // 2. Si el usuario ha añadido fotos nuevas en el proceso de edición, las subimos
        if (photos && photos.length > 0) {
          this.houseService.uploadHouseImages(id, photos).subscribe({
            next: () => this.finishSubmit(tituloLimpio, id, true),
            error: () => this.finishSubmit(tituloLimpio, id, false, true) // Error subiendo fotos
          });
        } else {
          // Si no hay fotos nuevas, terminamos
          this.finishSubmit(tituloLimpio, id, true);
        }
      },
      error: (err) => {
        console.error(err);
        this.isLoading.set(false);

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
        this.isLoading.set(true);
        this.houseService.deleteHouseImage(id, imageUrl).subscribe({
          next: () => {
            //Quitamos la URL del signal para que desaparezca de la pantalla automáticamente
            const filteredUrls = this.existingPhotosUrls().filter(url => url !== imageUrl);
            this.existingPhotosUrls.set(filteredUrls);

            this.isLoading.set(false);
          },
          error: (err) => {
            console.error(err);
            this.isLoading.set(false);
            Swal.fire('Error', 'No se pudo borrar la imagen.', 'error');
          }
        });
      }
    });
  }

}
