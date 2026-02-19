import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {HouseService} from '../../services/house/house-service';
import {HouseCard} from '../shared/house-card/house-card';
import {EntityCardView} from '../shared/Grids/entity-card-view/entity-card-view';
import {PageResponse} from '../../Models/Shared/PageResponse';

@Component({
  selector: 'app-explore',
  imports: [HouseCard, EntityCardView],
  templateUrl: './explore.html',
  styleUrl: './explore.scss',
})
export class Explore implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);

  houses = signal<PageResponse<HouseCardResponse> | null>(null);
  isLoading = signal(false);

  ngOnInit() {
    this.loadHouses(0);
  }

  loadHouses(page: number) {
    this.isLoading.set(true);
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

  onPageChange(newPage: number) {
    this.loadHouses(newPage);
  }

  protected readonly HouseCard = HouseCard;
}
