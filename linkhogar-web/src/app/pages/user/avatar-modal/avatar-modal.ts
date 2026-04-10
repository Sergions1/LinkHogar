import {Component, inject} from '@angular/core';
import {ImageCroppedEvent, ImageCropperComponent} from 'ngx-image-cropper';
import {UserService} from '../../../services/user/user-service';
import {AuthService} from '../../../services/auth/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-avatar-modal',
  imports: [
    ImageCropperComponent
  ],
  templateUrl: './avatar-modal.html'
})
export class AvatarModal {
  imageChangedEvent: any = '';
  croppedImage: Blob | null | undefined = null;

  private userService = inject(UserService);
  private authService = inject(AuthService);

  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.blob;
  }

  saveAvatar() {
    if (!this.croppedImage) return;

    const formData = new FormData();
    formData.append('file', this.croppedImage, 'avatar.jpg');

    const userId = this.authService.currentUser()?.id;
    if(!userId) {
      Swal.fire({
        title: 'Error',
        text: 'Ha ocurrido un error al subir la foto',
        icon: 'error',
        confirmButtonText: 'Aceptar',
        confirmButtonColor: 'var(--color-acento)'
      });
      return;
    }

    this.userService.uploadAvatar(userId, formData).subscribe({
      next: (res) => {
        this.CleanModal();

        this.authService.fetchCurrentUser();

        Swal.fire({
          title: '¡Foto actualizada!',
          text: 'Tu avatar se ha cambiado correctamente.',
          icon: 'success',
          confirmButtonColor: 'var(--color-acento)'
        });
      },
      error: () => {
        Swal.fire({
          title: 'Error',
          text: 'Hubo un problema al subir la imagen.',
          icon: 'error',
          confirmButtonColor: 'var(--color-acento)'
        });
      }
    });
  }

  CleanModal() {
    this.imageChangedEvent = '';
    this.croppedImage = null;
  }
}
