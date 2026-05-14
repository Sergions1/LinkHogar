import { AddressResponse } from "../Address/addressResponse.interface";
import {RoomResponse} from './RoomResponse';
import {RentalMode} from '../../pipes/RentalModePipe'; // Asegúrate de importar tu modelo de Address

export interface HouseCardResponse {
  id: string;
  title: string;
  description: string;

  // 🕒 Las fechas en JSON viajan como Strings
  publicationDate: string;
  updateDate: string;

  houseType: string;
  status:string;
  publicationStatus: string;
  rentalMode: RentalMode;

  size: number;
  rooms: number;
  baths: number;
  price: number;
  address: AddressResponse;

  images: string;
  roomList: RoomResponse[];
}
