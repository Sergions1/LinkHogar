import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'gender',
  standalone: true
})
export class GenderPipe implements PipeTransform {

  // Diccionario de traducciones
  private readonly translations: Record<string, string> = {
    'MALE': 'Hombre',
    'FEMALE': 'Mujer',
    'OTHER': 'Otro'
  };

  transform(value: string | null | undefined): string {
    if (!value) return 'No indica';
    return this.translations[value] || 'No indica';
  }
}
