// Tipos literales que simulan tus Enums de Java
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type Occupation = 'STUDENT' | 'WORKER' | 'STUDY_AND_WORK' | 'NOT_DEFINED';

export interface TenantProfileResponse {
  gender: Gender | null;
  ageRange: string | null;
  occupation: Occupation | null;
  description: string | null;
  isSmoker: boolean | null;
  hasPets: boolean | null;
}
