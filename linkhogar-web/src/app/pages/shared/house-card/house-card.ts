import {Component, Input,inject} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';
import { LucideAngularModule} from 'lucide-angular';

@Component({
  selector: 'house-card',
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './house-card.html',
  styleUrl: './house-card.scss',
})
export class HouseCard {
  private router = inject(Router);

  @Input({ required: true }) house!: HouseCardResponse;

  open() {
    this.router.navigate(['/inmueble', this.house.title, this.house.id]);
  }
}
