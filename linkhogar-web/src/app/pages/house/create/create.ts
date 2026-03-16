// create.ts
import {Component, signal, computed, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { TypeStep } from './steps/type-step/type-step';
import { FeaturesStep, FeaturesData } from './steps/features-step/features-step';
import { PriceStep } from './steps/price-step/price-step';
import { PhotosStep } from './steps/photos-step/photos-step';
import { ReviewStep } from './steps/review-step/review-step';
import {UbicationData, UbicationStep} from './steps/ubication-step/ubication-step';
import {  AnnouncementDetailData,  AnnouncementDetailStep} from './steps/announcement-detail-step/announcement-detail-step';
import {HouseService} from '../../../services/house/house-service';
import {Router} from '@angular/router';
import Swal from 'sweetalert2';

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
export class Create {
  private houseService = inject(HouseService);
  private router = inject(Router);

  readonly steps = [
    { label: 'Location',    icon: 'bi-geo-alt' },
    { label: 'Type',        icon: 'bi-house' },
    { label: 'Features',    icon: 'bi-list-check' },
    { label: 'Price',       icon: 'bi-tag' },
    { label: 'Photos',      icon: 'bi-images' },
    { label: 'Desripción',  icon: 'bi-comments' },
    { label: 'Review',      icon: 'bi-check-circle' },
  ];

  currentStep = signal(0);
  stepValid = signal(false);
  isLoading = signal(false);

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

  next() {
    if (!this.isLast()) {
      this.stepValid.set(false);
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

  showSuccesfullCreating(){

  }

  submit() {
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

}
