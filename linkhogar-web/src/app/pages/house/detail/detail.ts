import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {ActivatedRoute} from '@angular/router';
import {HouseService} from '../../../services/house/house-service';

@Component({
  selector: 'app-detail',
  imports: [],
  templateUrl: './detail.html',
  styleUrl: './detail.scss',
})
export class Detail implements OnInit{
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);

 house= signal<HouseResponse | null>(null);
 isLoading = signal(false);

 houseId : string | null = null;


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.houseId = params.get('houseId');
      this.loadHouse();
    })
  }

  loadHouse() {
    this.isLoading.set(true);
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
