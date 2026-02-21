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

  /**
   * Obtiene una lista paginada de todas las viviendas
   *
   * @param page Numero de página que queremos obtener (Por defecto la primera)
   * @param size Numero de elementos en la página (Por defecto 10)
   * @returns Un objeto PageResponse con elementos HouseResponse*/
  getPaginatedHouses(page: number = 0, size: number= 10): Observable<PageResponse<HouseResponse>>{
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}`, {params});
  }

  /**
   * Obtiene una lista paginada de todas las viviendas de una ciudad
   *
   * @param city ciudad por la que se quiere buscar
   * @param page Numero de página que queremos obtener (Por defecto la primera)
   * @param size Numero de elementos en la página (Por defecto 10)
   * @returns Un objeto PageResponse con elementos HouseResponse*/
  getByCityPaginatedHouses(city:string, page: number = 0, size: number=10): Observable<PageResponse<HouseResponse>>{
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<HouseResponse>>(`${this.apiUrl}/city/${city}`, {params});
  }

  getHouseById( houseId:string): Observable<HouseResponse> {
    return this.http.get<HouseResponse>(`${this.apiUrl}/${houseId}`);
  }
}
