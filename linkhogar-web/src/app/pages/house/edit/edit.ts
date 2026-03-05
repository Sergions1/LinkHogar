import {Component, inject, signal, OnInit, Input} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HouseService} from '../../../services/house/house-service';
import {HouseResponse} from '../../../Models/Houses/HouseResponse';
import {HouseForm} from '../house-form/house-form';


@Component({
  selector: 'app-edit',
  imports: [HouseForm], // 👈 añadirlo aquí
  templateUrl: './edit.html',
  styleUrl: './edit.scss',
})
export class Edit implements OnInit {
  private route = inject(ActivatedRoute);
  private houseService = inject(HouseService);

  house = signal<HouseResponse | null>(null);
  isLoading = signal(false);

  ngOnInit() {
    this.loadHouse();
  }

  loadHouse() {
    this.isLoading.set(true);
    this.route.paramMap.subscribe(param => {
      const id = param.get('id');
      if (id != null) {
        this.houseService.getHouseById(id).subscribe({
          next: (data) => {
            this.house.set(data);
            this.isLoading.set(false);
          },
          error: (err) => {
            console.error(err);
            this.isLoading.set(false);
          }
        });
      }
    });
  }

  updateHouse(data: any) { // 👈 acepta $event
    console.log('Actualizando casa:', data);
  }
}
