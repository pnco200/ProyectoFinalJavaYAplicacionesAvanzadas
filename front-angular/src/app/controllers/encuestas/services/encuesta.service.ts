
import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Encuesta, TipoEncuesta } from '../models/encuesta.model';

@Injectable({
  providedIn: 'root'
})
export class EncuestaService {

  private apiUrl = `${environment.apiUrl}/encuesta`;

  constructor(private http: HttpClient) { }

  findById(id: number): Observable<Encuesta> {
    return this.http.get<Encuesta>(`${this.apiUrl}/findById/${id}`);
  }

  findByTipo(tipo: TipoEncuesta): Observable<Encuesta[]> {
    return this.http.get<Encuesta[]>(`${this.apiUrl}/findByTipo/${tipo}`);
  }

  findByViviendaId(viviendaId: number): Observable<Encuesta[]> {
    return this.http.get<Encuesta[]>(`${this.apiUrl}/findByViviendaId/${viviendaId}`);
  }

  findByCampañaId(campaniaId: number): Observable<Encuesta[]> {
    return this.http.get<Encuesta[]>(`${this.apiUrl}/findByCampaniaId/${campaniaId}`);
  }

  findAll(): Observable<Encuesta[]> {
    return this.http.get<Encuesta[]>(`${this.apiUrl}/all`);
  }

  create(tipo: TipoEncuesta, viviendaId?: number, campaniaId?: number): Observable<Encuesta> {
    let params = new HttpParams().set('tipo', tipo);
    if (viviendaId) {
      params = params.set('viviendaId', viviendaId.toString());
    }
    if (campaniaId) {
      params = params.set('campaniaId', campaniaId.toString());
    }
    return this.http.post<Encuesta>(`${this.apiUrl}/create`, null, { params });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
