import { AddressResponse } from "../Address/addressResponse.interface";
import {UserResponse} from '../Users/UserResponse';


export interface HouseResponse {
  id: string; // Los UUID en JSON son strings
  title: string;
  description: string;

  // 📅 Las fechas en JSON siempre son strings
  creationDate: string;
  publicationDate: string; // Ojo: Si puede ser null en Java, pon "string | null"
  updateDate: string;

  // 🏷️ Enums: Puedes usar 'string' o valores exactos (Union Types)
  houseType: string;
  publicationStatus: string;
  status: string;

  size: number;
  rooms: number;
  baths: number;

  // ✅ Booleanos (En interfaces no se ponen valores por defecto)
  lift: boolean;
  furnished: boolean;
  airConditioned: boolean;
  terrace: boolean;
  balcony: boolean;
  garage: boolean;
  storage: boolean;
  pool: boolean;
  commonAreas: boolean;
  petsAllowed: boolean;

  price: number;

  images: string[];

  // 🏠 Objetos anidados
  address: AddressResponse;
  owner: UserResponse;
}
