import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'expenseCategory',
  standalone: true
})
export class ExpenseCategoryPipe implements PipeTransform {

  transform(value: string | undefined): string {
    if (!value) return 'Desconocido';

    switch (value.toUpperCase()) {
      case 'ALQUILER':
        return 'Alquiler';
      case 'SUPERMERCADO':
        return 'Supermercado';
      case 'SUMINISTROS':
        return 'Suministros';
      case 'INTERNET':
        return 'Internet';
      case 'LIMPIEZA':
        return 'Limpieza';
      case 'OTROS':
        return 'Otros';
      default:
        return value;
    }
  }

}
