import { Pipe, PipeTransform } from '@angular/core';

export type RentalMode = 'COMPLETE' | 'BY_ROOM';

@Pipe({
  name: 'rentalMode',
  standalone: true
})
export class RentalModePipe implements PipeTransform {

  private readonly labels: Record<RentalMode, string> = {
    'COMPLETE': 'Propiedad completa',
    'BY_ROOM': 'Por habitaciones'
  };

  private readonly icons: Record<RentalMode, string> = {
    'COMPLETE': 'bi-house-door',
    'BY_ROOM': 'bi-door-closed'
  };

  transform(value: RentalMode | string | null | undefined, type: 'label' | 'icon' = 'label'): string {
    const mode = value as RentalMode;

    if (!mode || !this.labels[mode]) {
      return value || '';
    }

    return type === 'label' ? this.labels[mode] : this.icons[mode];
  }
}

export const RentalModeTranslator = {
  // Del Backend (JSON) al Dominio/Frontend
  toFrontend(mode: string): RentalMode {
    if (mode === 'BY_ROOM') return 'BY_ROOM';
    return 'COMPLETE';
  },

  // Del Frontend al Backend (Payload del POST/PUT)
  toBackend(mode: RentalMode): string {
    return mode; // En este caso coinciden los strings, pero aquí podrías cambiar el formato si fuera necesario
  }
};
