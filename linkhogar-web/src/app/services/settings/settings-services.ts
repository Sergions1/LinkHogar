import {inject, Injectable, signal} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SettingsServices {
  private http = inject(HttpClient)
  private apiUrl = `${environment.apiUrl}/AppSettings`;

  heroImage = signal<string>('');
  logoImage = signal<string>('');
  publishImage = signal<string | null>(null);
  faviconImage = signal<string | null>(null);

  allSettings = signal<Record<string, string>>({});

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

  loadAllSettings(): Observable<Record<string, string>> {
    return this.http.get<Record<string, string>>(this.apiUrl).pipe(
      tap((settingsMap) => {
        this.allSettings.set(settingsMap);

        if (settingsMap['HERO_INITIAL_IMAGE']) this.heroImage.set(settingsMap['HERO_INITIAL_IMAGE']);
        if (settingsMap['APP_LOGO'])           this.logoImage.set(settingsMap['APP_LOGO']);
        if (settingsMap['PUBLISH_IMAGE'])      this.publishImage.set(settingsMap['PUBLISH_IMAGE']);
        if (settingsMap['FAVICON'])            this.faviconImage.set(settingsMap['FAVICON']);
      })
    );
  }

  updateSettingImage(name: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.put(`${this.apiUrl}/${name}/image`, formData, { headers, responseType: 'text' });
  }
}
