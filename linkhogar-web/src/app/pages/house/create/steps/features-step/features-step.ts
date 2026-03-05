// steps/features-step/features-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
}

const DEFAULT: FeaturesData = {
  size: null, rooms: null, baths: null,
  lift: false, furnished: false, airConditioned: false,
  terrace: false, balcony: false, garage: false,
  pool: false, petsAllowed: false
};

@Component({
  selector: 'app-features-step',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './features-step.html',
})
export class FeaturesStep implements OnInit {
  @Input() data!: FeaturesData;
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<FeaturesData>();

  form = signal<FeaturesData>({ ...DEFAULT });

  readonly toggles: { key: keyof FeaturesData; label: string; icon: string }[] = [
    { key: 'lift',          label: 'Lift',           icon: 'bi-arrow-up-square' },
    { key: 'furnished',     label: 'Furnished',      icon: 'bi-lamp-fill' },
    { key: 'airConditioned',label: 'A/C',            icon: 'bi-wind' },
    { key: 'terrace',       label: 'Terrace',        icon: 'bi-flower1' },
    { key: 'balcony',       label: 'Balcony',        icon: 'bi-door-open' },
    { key: 'garage',        label: 'Garage',         icon: 'bi-car-front' },
    { key: 'pool',          label: 'Pool',           icon: 'bi-droplet' },
    { key: 'petsAllowed',   label: 'Pets allowed',   icon: 'bi-hearts' },
  ];

  ngOnInit() {
    if (this.data) this.form.set({ ...this.data });
    this.emitValidity();
  }

  updateNumber(field: 'size' | 'rooms' | 'baths', value: string) {
    const parsed = parseInt(value, 10);
    this.form.update(f => ({ ...f, [field]: isNaN(parsed) ? null : parsed }));
    this.emitChanges();
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
      && f.baths !== null && f.baths > 0;
    this.validChange.emit(valid);
  }
}
