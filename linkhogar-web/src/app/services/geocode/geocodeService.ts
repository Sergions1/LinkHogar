import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {City, GeonamesResponse} from '../../Models/Shared/cityInterface';

/**
 * Servicio encargado de la búsqueda y geocodificación de municipios en España.
 * Se integra con la API REST pública de Geonames para obtener datos geográficos estructurados.
 */
@Injectable({
  providedIn: 'root',
})
export class GeocodeService {
  private http = inject(HttpClient);
  private username = 'Sergions1';

  /**
   * Realiza una consulta a la API de Geonames para buscar poblaciones en España que coincidan con el texto indicado.
   * Restringe la búsqueda a lugares poblados (featureClass=P) y limita la respuesta a un máximo de 8 resultados para optimizar el rendimiento del autocompletado.
   *
   * @param {string} query - El término de búsqueda introducido por el usuario (ej. "Huelva" o "Mad").
   * @returns {Observable<City[]>} Un observable que emite un array de ciudades, ya transformadas al modelo interno de la aplicación.
   */
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

  /**
   * Transforma una cadena de texto convencional en un "slug" limpio, ideal para ser utilizado
   * como parámetro en el enrutamiento de Angular (SEO-friendly).
   * * Aplica un proceso de normalización que incluye:
   * 1. Conversión a minúsculas.
   * 2. Descomposición Unicode (NFD) para separar los caracteres de sus marcas diacríticas (tildes).
   * 3. Eliminación de las tildes mediante expresión regular.
   * 4. Sustitución de bloques de espacios por guiones simples.
   * 5. Limpieza final eliminando cualquier carácter que no sea alfanumérico o guion.
   *
   * @param {string} texto - La cadena de texto original (ej. "La Coruña").
   * @returns {string} La cadena formateada como slug (ej. "la-coruna").
   */
  toSlug(texto: string): string {
    return texto
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-')
      .replace(/[^a-z0-9-]/g, '');
  }
}
