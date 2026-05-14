import {TenantProfileResponse} from './TenantProfileResponse';

export interface RoomResponse {
  id: string;
  name: string;
  description: string;
  price: number;
  size: number;
  hasPrivateBath: boolean;
  bedType: string;
  status: string;
  currentTenant?: TenantProfileResponse;
  images: string[]; // 👈 El nombre exacto que viene del backend
}
