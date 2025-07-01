import { Coordenadas } from "../../coordenadas/models/coordenadas.model";

export interface Barrio {
    id?: number;
    nombre: string;
    coordenadas: Coordenadas[];
  }