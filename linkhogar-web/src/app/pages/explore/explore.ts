import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {HouseService} from '../../services/house/house-service';
import {JsonPipe} from '@angular/common';

@Component({
  selector: 'app-explore',
  imports: [
    JsonPipe
  ],
  templateUrl: './explore.html',
  styleUrl: './explore.scss',
})
export class Explore implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);

  houses: HouseCardResponse[] = [];
  cityFilter: string = '';

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.cityFilter = params['city'];

      if (this.cityFilter) {
        this.houseService.searchByCity(this.cityFilter).subscribe({
          next: (data) => {
            this.houses = data;
          }
        });
      }
    });
  }
}
