import {Component, inject, signal, OnInit, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HouseService} from '../../../services/house/house-service';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';

@Component({
  selector: 'app-edit',
  imports: [],
  templateUrl: './edit.html',
  styleUrl: './edit.scss',
})
export class Edit implements OnInit{
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);

  house= signal<HouseResponse | null>(null);
  isLoading = signal(false);

  @Input() houseId : string | null = null;

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

  updateHouse(){

  }
}
