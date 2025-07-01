
import { Renglon } from "../../renglones/models/renglon.model";

export interface Encuesta {
    id?: number;
    tipo: TipoEncuesta;
    viviendaId?: number;
    campaniaId?: number;
    renglones?: Renglon[];
    personas?: Encuesta[];
  }

  export enum TipoEncuesta {
    PERSONA = 'PERSONA',
    VIVIENDA = 'VIVIENDA',
  }
