// steps/photos-step/photos-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface PhotoFile {
  file: File;
  preview: string;
}

@Component({
  selector: 'app-photos-step',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './photos-step.html',
})
export class PhotosStep implements OnInit {
  @Input() data!: File[];
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<File[]>();

  photos = signal<PhotoFile[]>([]);
  isDragging = signal(false);

  readonly MAX_PHOTOS = 10;
  readonly MIN_PHOTOS = 1;

  ngOnInit() {
    if (this.data?.length) {
      const previews = this.data.map(file => ({
        file,
        preview: URL.createObjectURL(file)
      }));
      this.photos.set(previews);
      this.emitValidity();
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) this.addFiles(Array.from(input.files));
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave() {
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging.set(false);
    if (event.dataTransfer?.files) {
      this.addFiles(Array.from(event.dataTransfer.files));
    }
  }

  private addFiles(files: File[]) {
    const imageFiles = files.filter(f => f.type.startsWith('image/'));
    const remaining = this.MAX_PHOTOS - this.photos().length;
    const toAdd = imageFiles.slice(0, remaining).map(file => ({
      file,
      preview: URL.createObjectURL(file)
    }));

    this.photos.update(p => [...p, ...toAdd]);
    this.dataChange.emit(this.photos().map(p => p.file));
    this.emitValidity();
  }

  remove(index: number) {
    this.photos.update(p => {
      URL.revokeObjectURL(p[index].preview);
      return p.filter((_, i) => i !== index);
    });
    this.dataChange.emit(this.photos().map(p => p.file));
    this.emitValidity();
  }

  moveLeft(index: number) {
    if (index === 0) return;
    this.photos.update(p => {
      const arr = [...p];
      [arr[index - 1], arr[index]] = [arr[index], arr[index - 1]];
      return arr;
    });
  }

  moveRight(index: number) {
    if (index === this.photos().length - 1) return;
    this.photos.update(p => {
      const arr = [...p];
      [arr[index + 1], arr[index]] = [arr[index], arr[index + 1]];
      return arr;
    });
  }

  private emitValidity() {
    this.validChange.emit(this.photos().length >= this.MIN_PHOTOS);
  }
}
