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
    if (!this.settingsService.logoImage() && !this.settingsService.heroImage()) {
      this.settingsService.loadAllSettings().subscribe();
    }
  }

  openCropper(settingName: string) {
    this.activeSettingName = settingName;
    this.cleanModal();

    if (settingName === 'HERO_INITIAL_IMAGE') {
      this.maintainAspectRatio = true;
      this.aspectRatio = 16 / 9; // Formato panorámico para la web
    } else if (settingName === 'APP_LOGO') {
      this.maintainAspectRatio = false; // El logo tiene formato libre
    } else if (settingName === 'PUBLISH_IMAGE') {
      this.maintainAspectRatio = true;
      this.aspectRatio = 16 / 9;
    } else if (settingName === 'FAVICON') {
      this.maintainAspectRatio = false;
      this.aspectRatio = 1 / 1;
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
    const fileNames: Record<string, string> = {
      'APP_LOGO':            'logo.png',
      'HERO_INITIAL_IMAGE':  'hero.jpg',
      'PUBLISH_IMAGE':       'publish.jpg',  // NUEVO
      'FAVICON':             'favicon.png',  // NUEVO (png para transparencia)
    };
    const fileName = fileNames[this.activeSettingName] ?? 'image.png';
    const file = new File([this.croppedImage], fileName, { type: this.croppedImage.type });

    this.settingsService.updateSettingImage(this.activeSettingName, file).subscribe({
      next: (newUrl) => {
        const signalMap: Record<string, (url: string) => void> = {
          'HERO_INITIAL_IMAGE': (url) => this.settingsService.heroImage.set(url),
          'APP_LOGO':           (url) => this.settingsService.logoImage.set(url),
          'PUBLISH_IMAGE':      (url) => this.settingsService.publishImage.set(url),  // NUEVO
          'FAVICON':            (url) => this.settingsService.faviconImage.set(url),  // NUEVO
        };
        signalMap[this.activeSettingName]?.(newUrl);

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
