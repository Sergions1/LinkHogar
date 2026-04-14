// steps/review-step/review-step.ts
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HouseForm } from '../../create';

@Component({
  selector: 'app-review-step',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review-step.html',
})
export class ReviewStep implements OnInit {
  @Input() data!: HouseForm;
  @Output() validChange = new EventEmitter<boolean>();
  @Input() existingPhotos: string[] = [];

  ngOnInit() {
    this.validChange.emit(true);
  }

  getToggleLabel(key: string): string {
    const labels: Record<string, string> = {
      lift: 'Lift', furnished: 'Furnished', airConditioned: 'A/C',
      terrace: 'Terrace', balcony: 'Balcony', garage: 'Garage',
      pool: 'Pool', petsAllowed: 'Pets allowed'
    };
    return labels[key] ?? key;
  }

  getToggleIcon(key: string): string {
    const icons: Record<string, string> = {
      lift: 'bi-arrow-up-square', furnished: 'bi-lamp-fill',
      airConditioned: 'bi-wind', terrace: 'bi-flower1',
      balcony: 'bi-door-open', garage: 'bi-car-front',
      pool: 'bi-droplet', petsAllowed: 'bi-hearts'
    };
    return icons[key] ?? 'bi-check';
  }

  get activeToggles(): string[] {
    const f = this.data.features; // 👈 antes 'caracteristicas'
    return ['lift','furnished','airConditioned','terrace','balcony','garage','pool','petsAllowed']
      .filter(k => f[k as keyof typeof f]);
  }

  getPreview(file: File): string {
    return URL.createObjectURL(file);
  }
}
