import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Renglon } from '../models/renglon.model';

@Injectable({
  providedIn: 'root'
})
export class RenglonService {

  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/renglon';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Renglon[]> {
    return this.http.get<Renglon[]>(`${this.apiUrl}/`);
  }

  findById(id: number): Observable<Renglon> {
    return this.http.get<Renglon>(`${this.apiUrl}/findById/${id}`);
  }

  findByEncuestaId(id: number): Observable<Renglon[]> {
    return this.http.get<Renglon[]>(`${this.apiUrl}/findByEncuestaId/${id}`);
  }

  findByTipo(tipo: string): Observable<Renglon[]> {
    return this.http.get<Renglon[]>(`${this.apiUrl}/findByTipo/${tipo}`);
  }

  create(renglon: Renglon): Observable<Renglon> {
    let params = new HttpParams()
      .set('tipo', renglon.tipo)
      .set('valor', renglon.valor)
      .set('encuestaId', renglon.encuestaId.toString());
    return this.http.post<Renglon>(`${this.apiUrl}/create`, null, { params });
  }

  update(renglon: Renglon): Observable<Renglon> {
    let params = new HttpParams();
    if (renglon.id) {
      params = params.set('id', renglon.id.toString());
    }
    if (renglon.valor) {
      params = params.set('valor', renglon.valor);
    }
    if (renglon.tipo) {
      params = params.set('tipo', renglon.tipo);
    }
    if (renglon.encuestaId) {
      params = params.set('encuestaId', renglon.encuestaId.toString());
    }
    return this.http.put<Renglon>(`${this.apiUrl}/update`, null, { params });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
