import { Coordenadas } from "../../coordenadas/models/coordenadas.model";

export interface Zona {
    id?: number;
    color: string;
    barrioId: number;
    coordenadas: Coordenadas[];
  }