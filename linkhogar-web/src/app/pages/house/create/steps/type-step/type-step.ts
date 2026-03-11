// steps/type-step/type-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

const TYPES = [
  { value: 'Piso',        icon: 'bi-building',        label: 'Piso' },
  { value: 'Adosado',     icon: 'bi-house-door',      label: 'Adosado' },
  { value: 'Estudio',     icon: 'bi-lamp',            label: 'Estudio' },
  { value: 'Apartamento', icon: 'bi-building-gear',   label: 'Apartamento' },
  { value: 'Chalet',      icon: 'bi-house-heart',     label: 'Chalet' },
  { value: 'Atico',       icon: 'bi-building-up',     label: 'Ático' },
  { value: 'Loft',        icon: 'bi-layout-text-sidebar', label: 'Loft' },
  { value: 'Habitacion',  icon: 'bi-door-closed',     label: 'Habitación' },
  { value: 'Residencia',  icon: 'bi-houses',          label: 'Residencia' },
];

@Component({
  selector: 'app-type-step',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './type-step.html',
})
export class TypeStep implements OnInit {
  @Input() data!: string;
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<string>();

  readonly types = TYPES;
  selected = signal<string | null>(null);

  ngOnInit() {
    if (this.data) {
      this.selected.set(this.data);
      this.validChange.emit(true);
    }
  }

  select(type: string) {
    this.selected.set(type);
    this.dataChange.emit(type);
    this.validChange.emit(true);
  }
}
