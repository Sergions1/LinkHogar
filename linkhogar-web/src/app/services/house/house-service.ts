import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HouseService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/houses`;

  searchByCity(city: string): Observable<HouseCardResponse[]> {
    return this.http.get<HouseCardResponse[]>(`${this.apiUrl}/search`,{
      params: {city: city}
    });
  }
}
