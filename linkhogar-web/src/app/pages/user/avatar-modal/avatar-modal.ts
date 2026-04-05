import {Component, inject} from '@angular/core';
import {ImageCroppedEvent, ImageCropperComponent} from 'ngx-image-cropper';
import {UserService} from '../../../services/user/user-service';

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

  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.blob;
  }

  subirAvatar() {
    if (!this.croppedImage) return;

    const formData = new FormData();
    formData.append('file', this.croppedImage, 'avatar.jpg');

    this.userService.uploadAvatar(formData).subscribe({
      next: (res) => {
        this.limpiarModal();
      }
    });
  }

  limpiarModal() {
    this.imageChangedEvent = '';
    this.croppedImage = null;
  }
}
