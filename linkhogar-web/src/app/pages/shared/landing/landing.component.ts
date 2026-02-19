import {Component, inject, signal} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {GeocodeService} from '../../../services/geocode/geocodeService';
import {City} from '../../../Models/Shared/cityInterface';
import {debounceTime, distinctUntilChanged, Subject, switchMap} from 'rxjs';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-landing',
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  private router = inject(Router);
  private geoCodeService = inject(GeocodeService);

  searchCity: String = "";

  searchQuery = signal('');
  sugerencias = signal<City[]>([]);
  selectedCity = signal<City | null>(null);
  isSearching = signal(false);

  private searchSubject = new Subject<string>();

  constructor() {
    // Esperamos 300ms tras dejar de escribir para llamar a la API
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (query.length < 3) {
          this.sugerencias.set([]);
          return [];
        }
        this.isSearching.set(true);
        return this.geoCodeService.search(query);
      })
    ).subscribe({
      next: (municipios) => {
        this.sugerencias.set(municipios);
        this.isSearching.set(false);
      },
      error: () => this.isSearching.set(false)
    });
  }

  onInput(event: Event) {
    const valor = (event.target as HTMLInputElement).value;
    this.searchQuery.set(valor);
    this.selectedCity.set(null); // resetea selección al escribir
    this.searchSubject.next(valor);
  }

  seleccionar(city: City) {
    this.selectedCity.set(city);
    this.searchQuery.set(city.display);
    this.sugerencias.set([]);
  }

  search(){
    const city = this.selectedCity();
    if (city) {
      this.router.navigate(['/explore', city.slugProvince, city.slug]);
    }
  }
}
