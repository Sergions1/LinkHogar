import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {City, GeonamesResponse} from '../../Models/Shared/cityInterface';

@Injectable({
  providedIn: 'root',
})
export class GeocodeService {
  private http = inject(HttpClient);
  private username = 'Sergions1';

  search(query: string): Observable<City[]> {
    const url = `http://api.geonames.org/searchJSON?country=ES&featureClass=P&q=${query}&maxRows=8&username=${this.username}`;

    return this.http.get<GeonamesResponse>(url).pipe(
      map(res => res.geonames.map(g => this.map(g)))
    );
  }

  private map(g: any): City {
    return {
      id: g.geonameId,
      name: g.name,
      province: g.adminName1,
      slug: this.toSlug(g.name),
      slugProvince: this.toSlug(g.adminName1),
      display: `${g.name}, ${g.adminName1}`
    };
  }

  toSlug(texto: string): string {
    return texto
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-')
      .replace(/[^a-z0-9-]/g, '');
  }
}
