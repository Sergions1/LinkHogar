import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, from, Subject, switchMap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';

export interface UbicationData {
  street: string;
  number: string;
  city: string;
  province: string;
  cp: string;
  floor?: string;
  door?: string;
  latitude?: number;
  longitude?: number;
}

interface NominatimResult {
  lat: string;
  lon: string;
  display_name: string;
}

@Component({
  selector: 'app-ubication-step',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ubication-step.html',
})
export class UbicationStep implements OnInit {
  @Input() data!: UbicationData;
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<UbicationData>();

  private http = inject(HttpClient);

  form = signal<UbicationData>({
    street: '', number: '', city: '',
    province: '', cp: '', floor: '', door: '',
    latitude: undefined, longitude: undefined
  });

  isValidating = signal(false);
  isValid = signal(false);
  errorMessage = signal<string | null>(null);
  foundAddress = signal<string | null>(null);

  private searchSubject = new Subject<UbicationData>();

  ngOnInit() {
    if (this.data) this.form.set({ ...this.data });

    this.searchSubject.pipe(
      debounceTime(800),
      distinctUntilChanged(),
      switchMap(data => {
        this.isValidating.set(true);
        this.errorMessage.set(null);
        this.foundAddress.set(null);
        return from(this.validateAddress(data));
      })
    ).subscribe({
      next: (result) => {
        this.isValidating.set(false);
        if (result) {
          this.isValid.set(true);
          this.foundAddress.set(result.display_name);
          this.form.update(f => ({
            ...f,
            latitude: parseFloat(result.lat),
            longitude: parseFloat(result.lon)
          }));
          this.validChange.emit(true);
          this.dataChange.emit(this.form());
        } else {
          this.isValid.set(false);
          this.errorMessage.set('Dirección no encontrada. Comprueba los datos.');
          this.validChange.emit(false);
        }
      },
      error: () => {
        this.isValidating.set(false);
        this.isValid.set(false);
        this.errorMessage.set('Error al validar la dirección. Inténtalo de nuevo.');
        this.validChange.emit(false);
      }
    });
  }

  onFieldChange() {
    const f = this.form();
    this.isValid.set(false);
    this.validChange.emit(false);
    this.foundAddress.set(null);
    if (f.street.trim() && f.number.trim() && f.city.trim() && f.cp.trim()) {
      this.searchSubject.next(f);
    }
  }

  update(field: keyof UbicationData, value: string) {
    this.form.update(f => ({ ...f, [field]: value }));
    this.onFieldChange();
  }

  private async validateAddress(data: UbicationData): Promise<NominatimResult | null> {
    const query = `${data.street} ${data.number}, ${data.cp} ${data.city}, ${data.province}, España`;
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1&countrycodes=es`;

    try {
      const results = await this.http.get<NominatimResult[]>(url, {
        headers: { 'Accept-Language': 'es' }
      }).toPromise();

      if (results && results.length > 0) {
        const result = results[0];
        if (result.display_name.includes(data.cp)) {
          return result;
        }
        // 👇 CP no coincide con el resultado
        return null;
      }
      return null;
    } catch {
      return null;
    }
  }
}
