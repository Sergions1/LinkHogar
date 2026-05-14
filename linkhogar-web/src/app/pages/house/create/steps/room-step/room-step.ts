import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RoomDetail } from '../../create';
import {debounceTime} from 'rxjs';

@Component({
  selector: 'app-rooms-step',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './room-step.html',
})
export class RoomsStep implements OnInit, OnChanges {
  @Input() roomsCount: number = 0;
  @Input() data: RoomDetail[] = [];
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<RoomDetail[]>();
  @Output() removeExistingImage = new EventEmitter<{roomIndex: number, url: string}>();

  private fb = inject(FormBuilder);
  roomsForm: FormArray = this.fb.array([]);
  private previewCache = new Map<File, string>();

  ngOnInit() {
    // La inicialización fuerte se hace en ngOnChanges
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['roomsCount'] || (changes['data'] && changes['data'].isFirstChange())) {
      this.syncForms();
    } else if (changes['data']) {
      const newData = changes['data'].currentValue as RoomDetail[];

      newData.forEach((room, i) => {
        if (i < this.roomsForm.length) {
          const formControl = this.roomsForm.at(i).get('existingPhotosUrls');
          const incomingUrls = room.existingPhotosUrls || [];

          if (formControl?.value?.length !== incomingUrls.length) {
            formControl?.setValue(incomingUrls, { emitEvent: false });
          }
        }
      });
    }
  }

  private syncForms() {
    const currentLength = this.roomsForm.length;
    const targetLength = this.roomsCount;

    // Añadir formularios si faltan
    if (currentLength < targetLength) {
      for (let i = currentLength; i < targetLength; i++) {
        const roomData = this.data[i];
        this.roomsForm.push(this.createRoomGroup(i, roomData));
      }
    }
    // Eliminar formularios si sobran
    else if (currentLength > targetLength) {
      for (let i = currentLength - 1; i >= targetLength; i--) {
        this.roomsForm.removeAt(i);
      }
    }

    this.emitChanges();
  }

  private createRoomGroup(index: number, roomData?: RoomDetail): FormGroup {
    const group = this.fb.group({
      id: [roomData?.id || null],
      name: [roomData?.name || `Habitación ${index + 1}`, Validators.required],
      description: [roomData?.description || ''],
      price: [roomData?.price || null, [Validators.required, Validators.min(1)]],
      size: [roomData?.size || null],
      bedType: [roomData?.bedType || 'INDIVIDUAL'],
      hasPrivateBath: [roomData?.hasPrivateBath || false],
      photos: [roomData?.photos || []], // Array de archivos
      existingPhotosUrls: [roomData?.existingPhotosUrls || []]
    });

    group.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(() => this.emitChanges());

    return group;
  }

  // --- GESTIÓN DE FOTOS POR HABITACIÓN ---

  onFileSelected(event: Event, roomIndex: number) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const files = Array.from(input.files).filter(f => f.type.startsWith('image/'));
      const control = this.roomsForm.at(roomIndex).get('photos');
      const currentPhotos = control?.value || [];

      // Añadimos las nuevas fotos al array existente
      control?.setValue([...currentPhotos, ...files]);
      this.emitChanges(); // Forzamos la emisión porque setValue a veces no dispara el subscribe
    }
  }

  removePhoto(roomIndex: number, photoIndex: number) {
    const control = this.roomsForm.at(roomIndex).get('photos');
    const currentPhotos = control?.value || [];
    const newPhotos = currentPhotos.filter((_: any, i: number) => i !== photoIndex);

    control?.setValue(newPhotos);
    this.emitChanges();
  }

  getPhotoPreview(file: File): string {
    // Si la foto no tiene una URL generada aún, se la creamos y la guardamos
    if (!this.previewCache.has(file)) {
      this.previewCache.set(file, URL.createObjectURL(file));
    }

    return this.previewCache.get(file)!;
  }

  private emitChanges() {
    this.dataChange.emit(this.roomsForm.value);
    this.validChange.emit(this.roomsForm.valid);
  }

  removeExistingPhoto(roomIndex: number, url: string) {
    this.removeExistingImage.emit({ roomIndex, url });
  }
}
