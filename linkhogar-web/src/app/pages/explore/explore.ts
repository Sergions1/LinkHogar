import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {HouseService} from '../../services/house/house-service';
import {HouseCard} from '../shared/house-card/house-card';
import {EntityCardView} from '../shared/Grids/entity-card-view/entity-card-view';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {GeocodeService} from '../../services/geocode/geocodeService';

@Component({
  selector: 'app-explore',
  imports: [HouseCard, EntityCardView],
  templateUrl: './explore.html',
  styleUrl: './explore.scss',
})
export class Explore implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);
  private geoCodeService = inject(GeocodeService);

  houses = signal<PageResponse<HouseCardResponse> | null>(null);
  isLoading = signal(false);

  // Filtros activos
  private citySlug: string | null = null;

  ngOnInit() {
    // Leemos los parámetros de ruta
    this.route.paramMap.subscribe(params => {
      console.log('params:', params.keys);
      console.log('municipio:', params.get('municipio'));
      this.citySlug = params.get('municipio');
      this.loadHouses(0);
    });
  }

  loadHouses(page: number) {
    this.isLoading.set(true);

    const city = this.citySlug
      ? this.slugToName(this.citySlug)
      : null;

    if(city != null){
      console.log('city enviada al backend:', city);
      this.houseService.getByCityPaginatedHouses(city, page).subscribe({
        next: (data) => {
          console.log('data completa:', data);
          console.log('content:', data.content);
          console.log('totalElements:', data.totalElements);
          this.houses.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
        console.log("ERROR -> House loafing failed: ", err);
        this.isLoading.set(false);
      }
      })
    }else{
      this.houseService.getPaginatedHouses(page, 5).subscribe({
        next: (data) => {
          this.houses.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.log("ERROR -> House loafing failed: ", err);
          this.isLoading.set(false);
        }
      });
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
}
