import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {ActivatedRoute, Router} from '@angular/router';
import {HouseService} from '../../../services/house/house-service';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-detail',
  imports: [
    DecimalPipe
  ],
  templateUrl: './detail.html',
  styleUrl: './detail.scss',
})
export class Detail implements OnInit{
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);
  private router = inject(Router);

 house= signal<HouseResponse | null>(null);
 isLoading = signal(false);

 houseId : string | null = null;


  ngOnInit() {
    this.loadHouse();
  }

  loadHouse() {
    this.isLoading.set(true);
    this.route.paramMap.subscribe(param => {
      this.houseId = param.get('id');
    })
    if(this.houseId != null){
      this.houseService.getHouseById(this.houseId).subscribe({
        next: params => {
          this.house.set(params);
          this.isLoading.set(false);
        },
        error: err => {
          console.log(err);
          this.isLoading.set(false);
        }
      })
    }
  }
}
