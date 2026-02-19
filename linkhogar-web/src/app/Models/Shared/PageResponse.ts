export interface PageResponse<T> {
  content: T[];         // Aquí viene el array de entidades
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;       // Página actual (empieza en 0)
  first: boolean;
  last: boolean;
  empty: boolean;
}
