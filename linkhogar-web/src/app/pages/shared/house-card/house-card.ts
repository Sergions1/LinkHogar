import {Component, Input, inject, Output, EventEmitter} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';
import { LucideAngularModule} from 'lucide-angular';

@Component({
  selector: 'house-card',
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './house-card.html',
  styleUrl: './house-card.scss',
})
export class HouseCard {
  private router = inject(Router);

  @Input({ required: true }) house!: HouseCardResponse;

  @Input() isMyPublications: boolean = false;

  @Output() edit = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>();

  open() {
    this.router.navigate(['/inmueble', this.house.title, this.house.id]);
  }

  onEdit(event: Event) {
    event.stopPropagation(); // 👈 Clave para que no se active el stretched-link
    this.edit.emit(this.house.id);
  }

  onDelete(event: Event) {
    event.stopPropagation(); // 👈 Clave para que no se active el stretched-link
    this.delete.emit(this.house.id);
  }
}
