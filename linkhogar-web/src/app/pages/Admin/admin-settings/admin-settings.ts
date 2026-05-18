import { Component, inject, OnInit } from '@angular/core';
import { ImageCroppedEvent, ImageCropperComponent } from 'ngx-image-cropper';
import Swal from 'sweetalert2';
import {SettingsServices} from '../../../services/settings/settings-services';
// Importa el modal nativo de Bootstrap si lo necesitas para cerrarlo manualmente
declare var bootstrap: any;

@Component({
  selector: 'app-admin-settings',
  standalone: true,
  imports: [ImageCropperComponent],
  templateUrl: './admin-settings.html',
  styleUrl: './admin-settings.scss'
})
export class AdminSettings implements OnInit {
  public settingsService = inject(SettingsServices);

  // Variables para el recortador
  imageChangedEvent: any = '';
  croppedImage: Blob | null | undefined = null;
  activeSettingName: string = '';

  // Variables dinámicas según el tipo de imagen
  maintainAspectRatio: boolean = true;
  aspectRatio: number = 16 / 9;
  isUploading: boolean = false;

  ngOnInit(): void {
    // Una sola llamada para traer la configuración completa
    this.settingsService.loadAllSettings().subscribe({
      next: (ajustes) => console.log('Ajustes cargados en memoria:', ajustes),
      error: (err) => console.error('Error cargando los ajustes generales', err)
    });
  }

  openCropper(settingName: string) {
    this.activeSettingName = settingName;
    this.cleanModal();

    if (settingName === 'HERO_INITIAL_IMAGE') {
      this.maintainAspectRatio = true;
      this.aspectRatio = 16 / 9; // Formato panorámico para la web
    } else if (settingName === 'APP_LOGO') {
      this.maintainAspectRatio = false; // El logo tiene formato libre
    }

    // Abrir modal usando Bootstrap de forma programática (o usa data-bs-toggle en el HTML)
    const modalElement = document.getElementById('settingsCropperModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
  }

  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.blob;
  }

  saveImage() {
    if (!this.croppedImage || !this.activeSettingName) return;

    this.isUploading = true;

    // Lo convertimos en File y decidimos extensión (PNG es mejor para logos por la transparencia)
    const fileName = this.activeSettingName === 'APP_LOGO' ? 'logo.png' : 'hero.jpg';
    const file = new File([this.croppedImage], fileName, { type: this.croppedImage.type });

    this.settingsService.updateSettingImage(this.activeSettingName, file).subscribe({
      next: (newUrl) => {
        // Actualizamos la señal en tiempo real para que la UI reaccione
        if (this.activeSettingName === 'HERO_INITIAL_IMAGE') {
          this.settingsService.heroImage.set(newUrl);
        } else if (this.activeSettingName === 'APP_LOGO') {
          this.settingsService.logoImage.set(newUrl);
        }

        this.closeAndCleanModal();

        Swal.fire({
          title: '¡Imagen actualizada!',
          text: 'La configuración se ha guardado correctamente.',
          icon: 'success',
          confirmButtonColor: 'var(--color-acento)'
        });
      },
      error: (err) => {
        this.isUploading = false;
        console.error(err);
        Swal.fire({
          title: 'Error',
          text: 'Hubo un problema al subir la imagen.',
          icon: 'error',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }

  cleanModal() {
    this.imageChangedEvent = '';
    this.croppedImage = null;
    this.isUploading = false;
  }

  private closeAndCleanModal() {
    this.cleanModal();
    const modalElement = document.getElementById('settingsCropperModal');
    if (modalElement) {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }
}
