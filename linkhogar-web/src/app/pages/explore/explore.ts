import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {HouseService} from '../../services/house/house-service';
import {HouseCard} from '../shared/house-card/house-card';
import {EntityCardView} from '../shared/Grids/entity-card-view/entity-card-view';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {GeocodeService} from '../../services/geocode/geocodeService';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-explore',
  imports: [HouseCard, EntityCardView, FormsModule],
  templateUrl: './explore.html',
  styleUrl: './explore.scss',
})
export class Explore implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private houseService = inject(HouseService);

  houses = signal<PageResponse<HouseCardResponse> | null>(null);
  isLoading = signal(false);

  searchTerm = '';
  selectedRentalMode = signal<string>('');
  private citySlug: string | null = null;

  filteredHousesPage = computed<PageResponse<HouseCardResponse> | null>(() => {
    const originalData = this.houses();
    if (!originalData) return null;

    // 2. 🌟 Extraemos el valor leyendo la señal con paréntesis ()
    const modeFilter = this.selectedRentalMode();

    // Si no hay filtro seleccionado, devolvemos los datos tal cual vinieron del backend
    if (!modeFilter) {
      return originalData;
    }

    // Filtrar el array de viviendas de la página actual comparando con el valor de la señal
    const filteredContent = originalData.content.filter(house =>
      house.rentalMode === modeFilter
    );

    // Devolvemos un nuevo objeto estructurado igual que la página, pero con el contenido filtrado
    return {
      ...originalData,
      content: filteredContent
    };
  });

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.citySlug = params.get('municipio');

      // 👇 si no hay param de ruta, leemos el queryParam ?q=
      if (!this.citySlug) {
        this.route.queryParamMap.subscribe(queryParams => {
          const q = queryParams.get('q');
          this.citySlug = q ? this.toSlug(q) : null;
          this.searchTerm = q ?? "";
          this.loadHouses(0);
        });
      } else {
        this.loadHouses(0);
      }
    });
  }

  loadHouses(page: number) {
    this.isLoading.set(true);

    const city = this.citySlug
      ? this.slugToName(this.citySlug)
      : null;

    if(city != null){
      this.houseService.getByCityPaginatedHouses(city, page).subscribe({
        next: (data) => {
          this.houses.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
        console.log("ERROR -> House loafing failed: ", err);
        this.isLoading.set(false);
      }
      })
    }
  }

  onSearch() {
    if (this.searchTerm.trim()) {
      this.router.navigate(['/explore'], { queryParams: { q: this.searchTerm } });
    } else {
      this.router.navigate(['/explore']);
    }
  }

  onPageChange(newPage: number) {
    this.loadHouses(newPage);
  }

  protected readonly HouseCard = HouseCard;

  private slugToName(slug: string): string {
    return slug
      .split('-')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  private toSlug(text: string): string {
    return text.toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-');
  }
}
