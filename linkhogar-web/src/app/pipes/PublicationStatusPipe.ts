import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'publicationStatus',
  standalone: true
})
export class PublicationStatusPipe implements PipeTransform {

  transform(value: string | undefined): string {
    if (!value) return 'Desconocido';

    switch (value.toUpperCase()) {
      case 'DRAFT':
        return 'Borrador';
      case 'PENDING_REVIEW':
        return 'En revisión';
      case 'PUBLISHED':
        return 'Publicado';
      case 'PAUSED':
        return 'Pausado';
      case 'EXPIRED':
        return 'Caducado';
      case 'ARCHIVED':
        return 'Archivado';
      default:
        return value;
    }
  }

}
