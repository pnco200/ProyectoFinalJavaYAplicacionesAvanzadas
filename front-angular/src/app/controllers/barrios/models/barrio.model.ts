import { Coordenadas } from "../../coordenadas/models/coordenadas.model";

export interface Barrio {
    id?: number;
    name: string;
    coordenadas: Coordenadas[];
  }