export interface User {
  id: number;
  mail: string;
  name: string;
  domicilio: string;
  activity: string;
  extra: string;
  rolId: number;
  barrio: string;
  roles: string[];
  is_active: boolean;
  password?: string;
  barrioId?: number;
}