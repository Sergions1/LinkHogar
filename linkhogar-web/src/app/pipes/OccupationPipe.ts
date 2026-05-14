import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'occupation',
  standalone: true
})
export class OccupationPipe implements PipeTransform {

  private readonly translations: Record<string, string> = {
    'STUDENT': 'Estudiante',
    'WORKER': 'Trabajador/a',
    'STUDY_AND_WORK': 'Estudia y trabaja',
    'NOT_DEFINED': 'No definido'
  };

  transform(value: string | null | undefined): string {
    if (!value || value === 'NOT_DEFINED') return 'No indica';
    return this.translations[value] || 'No indica';
  }
}
