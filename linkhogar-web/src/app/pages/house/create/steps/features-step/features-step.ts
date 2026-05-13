// steps/features-step/features-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RentalModePipe} from '../../../../../pipes/RentalModePipe';

export interface FeaturesData {
  size: number | null;
  rooms: number | null;
  baths: number | null;
  lift: boolean;
  furnished: boolean;
  airConditioned: boolean;
  terrace: boolean;
  balcony: boolean;
  garage: boolean;
  pool: boolean;
  petsAllowed: boolean;
  storage: boolean;
  commonAreas: boolean;
}

const DEFAULT: FeaturesData = {
  size: null, rooms: null, baths: null,
  lift: false, furnished: false, airConditioned: false,
  terrace: false, balcony: false, garage: false,
  pool: false, petsAllowed: false, storage: false, commonAreas: false
};

@Component({
  selector: 'app-features-step',
  standalone: true,
  imports: [CommonModule, FormsModule, RentalModePipe],
  templateUrl: './features-step.html',
})
export class FeaturesStep implements OnInit {
  @Input() data!: FeaturesData;
  @Input() rentalMode: 'COMPLETE' | 'BY_ROOM' | null = null;

  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<FeaturesData>();
  @Output() rentalModeChange = new EventEmitter<'COMPLETE' | 'BY_ROOM'>();

  form = signal<FeaturesData>({ ...DEFAULT });
  mode = signal<'COMPLETE' | 'BY_ROOM' | null>(null);

  readonly toggles: { key: keyof FeaturesData; label: string; icon: string }[] = [
    { key: 'lift',           label: 'Ascensor',            icon: 'bi-arrow-up-square' },
    { key: 'furnished',      label: 'Amueblado',           icon: 'bi-lamp-fill' },
    { key: 'airConditioned', label: 'Aire acondicionado',  icon: 'bi-wind' },
    { key: 'terrace',        label: 'Terraza',             icon: 'bi-flower1' },
    { key: 'balcony',        label: 'Balcón',              icon: 'bi-door-open' },
    { key: 'garage',         label: 'Garaje',              icon: 'bi-car-front' },
    { key: 'storage',        label: 'Trastero',            icon: 'bi-box-seam' },
    { key: 'pool',           label: 'Piscina',             icon: 'bi-droplet' },
    { key: 'commonAreas',    label: 'Zonas comunes',       icon: 'bi-people' },
    { key: 'petsAllowed',    label: 'Admite mascotas',     icon: 'bi-hearts' },
  ];

  ngOnInit() {
    if (this.data) this.form.set({ ...this.data });
    if (this.rentalMode) this.mode.set(this.rentalMode);
    this.emitValidity();
  }

  updateNumber(field: 'size' | 'rooms' | 'baths', value: string) {
    const parsed = parseInt(value, 10);
    this.form.update(f => ({ ...f, [field]: isNaN(parsed) ? null : parsed }));
    this.emitChanges();
  }

  updateMode(newMode: 'COMPLETE' | 'BY_ROOM') {
    this.mode.set(newMode);
    this.rentalModeChange.emit(newMode);
    this.emitValidity();
  }

  toggleFeature(key: keyof FeaturesData) {
    this.form.update(f => ({ ...f, [key]: !f[key] }));
    this.emitChanges();
  }

  private emitChanges() {
    this.dataChange.emit(this.form());
    this.emitValidity();
  }

  private emitValidity() {
    const f = this.form();
    const valid = f.size !== null && f.size > 0
      && f.rooms !== null && f.rooms > 0
      && f.baths !== null && f.baths > 0
      && this.mode() !== null;
    this.validChange.emit(valid);
  }
}
