export interface User {
  id?: number; 
  mail: string;
  password?: string; // solo para inputdto
  name: string;
  is_active: boolean;
  domicilio: string;
  activity: string;
  extra: string;
  barrio?: string; 
  roles?: string[]; 
}