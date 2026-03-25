export interface CreateUserByAdminRequest {
  firstName: string;
  lastName: string;
  mail: string;
  phone?: string;
  fechaNac?: string;
  role: string;
}
