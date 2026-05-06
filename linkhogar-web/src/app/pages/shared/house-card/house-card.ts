import {Component, Input, inject, Output, EventEmitter} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';
import { LucideAngularModule} from 'lucide-angular';
import {PublicationStatusPipe} from '../../../pipes/PublicationStatusPipe';

@Component({
  selector: 'house-card',
  imports: [CommonModule, LucideAngularModule, PublicationStatusPipe],
  templateUrl: './house-card.html',
  styleUrl: './house-card.scss',
})
export class HouseCard {
  private router = inject(Router);

  @Input({ required: true }) house!: HouseCardResponse;

  @Input() isMyPublications: boolean = false;

  @Output() edit = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>();
  @Output() manageMembers = new EventEmitter<string>();

  getBadgeColor(status: string | undefined): string {
    if (!status) return 'bg-secondary'; // Gris por defecto si viene vacío

    switch (status.toUpperCase()) {
      case 'PUBLISHED':
        return 'bg-success'; // Verde
      case 'ARCHIVED':
        return 'bg-danger'; // Rojo
      case 'PENDING_REVIEW':
        return 'bg-warning text-dark'; // Amarillo con texto oscuro
      case 'DRAFT':
        return 'bg-secondary'; // Gris
      case 'PAUSED':
        return 'bg-info text-dark'; // Azul clarito
      case 'EXPIRED':
        return 'bg-dark'; // Negro
      default:
        return 'bg-primary'; // Tu color por defecto
    }
  }

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

  onManageMembers(event: Event) {
    event.stopPropagation();
    this.manageMembers.emit(this.house.id);
  }

  protected readonly PublicationStatusPipe = PublicationStatusPipe;
}
