// steps/type-step/type-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

const TYPES = [
  { value: 'Piso',    icon: 'bi-building',   label: 'Flat' },
  { value: 'Casa',    icon: 'bi-house',       label: 'House' },
  { value: 'Chalet',  icon: 'bi-house-door',  label: 'Chalet' },
  { value: 'Estudio', icon: 'bi-lamp',        label: 'Studio' },
  { value: 'Atico',   icon: 'bi-building-up', label: 'Penthouse' },
  { value: 'Local',   icon: 'bi-shop',        label: 'Commercial' },
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
