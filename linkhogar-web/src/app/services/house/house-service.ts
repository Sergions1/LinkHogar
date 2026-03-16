import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {HouseCardResponse} from '../../Models/Houses/house-card-response.interface';
import {Observable} from 'rxjs';
import {PageResponse} from '../../Models/Shared/PageResponse';
import {HouseResponse} from '../../Models/Houses/HouseResponse';
import {HouseForm} from '../../pages/house/create/create';

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

  /**
   * Crea una nueva vivienda en el sistema
   *
   * @param data Objeto HouseForm con los datos del formulario de creación
   * @returns Un Observable con la respuesta de la creación
   */
  createHouse(data: HouseForm) {
    const payload = {
      title: data.details.title,
      description: data.details.description,
      houseType: data.type, // Asegura que coincida con tu Enum de Java
      publicationStatus: "PUBLISHED", // O el valor que corresponda de tu Enum
      status: "Disponible",
      size: data.features.size,
      rooms: data.features.rooms,
      baths: data.features.baths,
      price: data.price,
      street: data.location.street,
      number: Number(data.location.number),
      floor: data.location.floor,
      door: data.location.door,
      city: data.location.city,
      cp: Number(data.location.cp),
      province: data.location.province,
      country: "España", // Por defecto o lo añades a la vista
      lift: data.features.lift,
      furnished: data.features.furnished,
      airConditioned: data.features.airConditioned,
      terrace: data.features.terrace,
      balcony: data.features.balcony,
      garage: data.features.garage,
      storage: data.features.storage,
      pool: data.features.pool,
      commonAreas: data.features.commonAreas,
      petsAllowed: data.features.petsAllowed
    };

    const token = localStorage.getItem('token'); // O el servicio donde guardes tu JWT

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${environment.apiUrl}/houses`, payload, {headers});
  }

  /**
   * Sube las imágenes de una vivienda ya creada
   *
   * @param houseId El ID de la vivienda a la que pertenecen las fotos
   * @param files Un array de objetos File nativos seleccionados desde el input HTML
   * @returns Un Observable con la respuesta de la subida
   */
  uploadHouseImages(houseId: string, files: File[]): Observable<any> {
    const formData = new FormData();

    files.forEach(file => {
      formData.append('files', file);
    });

    const token = localStorage.getItem('token');

    // NO ponemos el 'Content-Type'.
    // Al usar FormData, el navegador (HttpClient) sabe que tiene que usar 'multipart/form-data'
    // y generar su propio "boundary" automáticamente. Si lo pusiéramos a mano, fallaría.
    const headers = new HttpHeaders({'Authorization': `Bearer ${token}`});

    return this.http.post(`${this.apiUrl}/${houseId}/images`, formData, {
      headers,
      responseType: 'text'
    });
  }
}
