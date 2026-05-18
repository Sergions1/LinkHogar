import {
  ApplicationConfig, inject,
  LOCALE_ID,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import {provideSweetAlert2} from '@sweetalert2/ngx-sweetalert2';
import {SettingsServices} from './services/settings/settings-services';

// Registrar los datos de la localización para 'es'
registerLocaleData(localeEs);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withFetch()),
    provideSweetAlert2(),

    // Inicializador de la aplicación: Angular esperará a que este Observable termine antes de arrancar
    provideAppInitializer(() => {
      const settingsService = inject(SettingsServices);
      return settingsService.loadAllSettings();
    }),

    // Proveer el LOCALE_ID para que la aplicación use 'es' por defecto
    { provide: LOCALE_ID, useValue: 'es' },
  ]
};
