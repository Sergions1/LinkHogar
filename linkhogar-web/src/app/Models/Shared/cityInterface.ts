// Lo que devuelve la API de Geonames
export interface GeonamesCity {
  geonameId: number;
  name: string;          // "Alcobendas"
  adminName1: string;    // "Madrid" (provincia)
  countryCode: string;   // "ES"
  population: number;
}

export interface GeonamesResponse {
  geonames: GeonamesCity[];
  totalResultsCount: number;
}

// Lo usado internamente en la app
export interface City {
  id: number;
  name: string;        // "Alcobendas"
  province: string;     // "Madrid"
  slug: string;          // "alcobendas"
  slugProvince: string; // "madrid"
  display: string;       // "Alcobendas, Madrid" → lo que ve el usuario
}
