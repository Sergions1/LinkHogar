// steps/price-step/price-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-price-step',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './price-step.html',
})
export class PriceStep implements OnInit {
  @Input() data!: number | null;
  @Input() rentalMode: 'COMPLETE' | 'BY_ROOM' = 'COMPLETE';
  @Input() minRoomPrice: number = 0;

  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<number | null>();

  price = signal<number | null>(null);

  readonly suggestions = [500, 750, 1000, 1250, 1500, 2000];

  ngOnInit() {
    if (this.rentalMode === 'BY_ROOM') {
      // Si es por habitaciones, el precio es automáticamente el mínimo
      this.price.set(this.minRoomPrice);
      this.dataChange.emit(this.minRoomPrice);
      this.validChange.emit(true);
    } else if (this.data) {
      this.price.set(this.data);
      this.emitValidity();
    }
  }

  onPriceInput(value: string) {
    if (this.rentalMode === 'BY_ROOM') return;

    const parsed = parseFloat(value);
    const result = isNaN(parsed) ? null : parsed;
    this.price.set(result);
    this.dataChange.emit(result);
    this.emitValidity();
  }

  selectSuggestion(value: number) {
    this.price.set(value);
    this.dataChange.emit(value);
    this.validChange.emit(true);
  }

  private emitValidity() {
    const p = this.price();
    this.validChange.emit(p !== null && p > 0);
  }
}
