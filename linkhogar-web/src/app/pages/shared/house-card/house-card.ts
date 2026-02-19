import { Component, Input } from '@angular/core';
import {RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';

@Component({
  selector: 'house-card',
  imports: [CommonModule,RouterLink  ],
  templateUrl: './house-card.html',
  styleUrl: './house-card.scss',
})
export class HouseCard {
  @Input({ required: true }) house!: HouseCardResponse;
}
