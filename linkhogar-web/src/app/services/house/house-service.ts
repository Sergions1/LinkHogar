import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {Observable} from 'rxjs';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {HouseResponse} from '../../Models/Houses/HouseResponse';

@Injectable({
  providedIn: 'root',
})
export class HouseService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/houses`;

  getPaginatedHouses(page: number = 0, size: number= 10): Observable<PageResponse<HouseResponse>>{
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}`, {params});
  }

  getByCityPaginatedHouses(city:string, page: number = 0, size: number=10): Observable<PageResponse<HouseResponse>>{
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}/${city}`, {params});
  }
}
