import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SettingsServices {
  private http = inject(HttpClient)
  private apiUrl = `${environment.apiUrl}/AppSettings`;

  heroImage = signal<string>('');

  getSettings(name: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/${name}`, {responseType: 'text'});
  }

  loadInitialSettings() {
    return this.http.get(`${this.apiUrl}/HERO_INITIAL_IMAGE`, { responseType: 'text' })
      .pipe(
        tap(url => {
          if (url) this.heroImage.set(url);
        })
      );
  }
}
