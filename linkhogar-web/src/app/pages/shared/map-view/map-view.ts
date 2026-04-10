import { Component, Input, signal, effect, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { GoogleMapsModule } from '@angular/google-maps';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [GoogleMapsModule],
  template: `
    @if (apiLoaded()) {
      <google-map
        height="400px"
        width="100%"
        [center]="center()"
        [zoom]="zoom">
        @if (markerPosition(); as pos) {
          <map-marker [position]="pos"></map-marker>
        }
      </google-map>
    } @else {
      <div class="d-flex justify-content-center align-items-center" style="height: 400px;">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando mapa...</span>
        </div>
      </div>
    }
  `,
  styles: [`
    google-map { display: block; border-radius: 12px; overflow: hidden; }
  `]
})
export class MapView implements OnInit {
  private cdr = inject(ChangeDetectorRef);

  @Input() set lat(value: number) { this.center.set({ lat: value, lng: this.center().lng }); }
  @Input() set lng(value: number) { this.center.set({ lat: this.center().lat, lng: value }); }

  center = signal<google.maps.LatLngLiteral>({ lat: 40.416775, lng: -3.703790 });
  markerPosition = signal<google.maps.LatLngLiteral | null>(null);
  zoom = 15;
  apiLoaded = signal(false);

  constructor() {
    effect(() => {
      this.markerPosition.set(this.center());
    });
  }

  ngOnInit() {
    this.cargarScriptDeGoogleMaps();
  }

  cargarScriptDeGoogleMaps() {
    // Usamos (window as any) para saltarnos la validación estricta de TypeScript
    if ((window as any).google?.maps?.importLibrary) {
      this.apiLoaded.set(true);
      return;
    }

    const scriptId = 'google-maps-script';
    if (document.getElementById(scriptId)) return;

    // Creamos el script con la configuración de Bootstrap de Google Maps
    const script = document.createElement('script');
    script.id = scriptId;
    // Usamos la URL de carga de librerías modernas
    script.innerHTML = `
      (g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"-"+t.toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=\`https://maps.\${c}apis.com/maps/api/js?\`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Rebelions: "+l):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({
        key: "${environment.googleMapsApiKey}",
        v: "weekly"
      });
    `;
    document.head.appendChild(script);

    // Esperamos un momento a que el cargador de Google inicialice el objeto global
    const checkGoogle = setInterval(() => {
      const checkGoogle = setInterval(() => {
        // Usamos (window as any) aquí también
        if ((window as any).google?.maps?.importLibrary) {
          this.apiLoaded.set(true);
          this.cdr.detectChanges();
          clearInterval(checkGoogle);
        }
      }, 100);
    }, 100);

    // Seguridad: dejar de buscar tras 5 segundos si falla
    setTimeout(() => clearInterval(checkGoogle), 5000);
  }
}
