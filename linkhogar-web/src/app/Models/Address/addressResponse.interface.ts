export interface AddressResponse{
  street : string;
  number : number | null;
  floor : string;
  door : string;
  city : string;
  cp : number | null;
  province : string;
  country : string;

  latitude : number;
  longitude : number;

}
