import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HouseCardResponse} from '../../../Models/Houses/house-card-response.interface';
import { LucideAngularModule, MapPinned } from 'lucide-angular';

@Component({
  selector: 'house-card',
  imports: [CommonModule, RouterLink, LucideAngularModule],
  templateUrl: './house-card.html',
  styleUrl: './house-card.scss',
})
export class HouseCard {
  @Input({ required: true }) house!: HouseCardResponse;
}
