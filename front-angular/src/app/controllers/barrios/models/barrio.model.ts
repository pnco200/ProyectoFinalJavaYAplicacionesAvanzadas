import { Coordenadas } from "../../coordenadas/models/coordenadas.model";

export interface Barrio {
    id?: number;
    nombre: string;
    name?: string;
    coordenadas: Coordenadas[];
  }