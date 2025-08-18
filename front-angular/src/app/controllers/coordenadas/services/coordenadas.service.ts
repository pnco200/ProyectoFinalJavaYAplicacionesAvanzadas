import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coordenadas } from '../models/coordenadas.model';

@Injectable({
  providedIn: 'root'
})
export class CoordenadasService {

  private apiUrl = `${environment.apiUrl}/coordenadas`;

  constructor(private http: HttpClient) { }

  find(id?: number, barrioId?: number, zonaId?: number): Observable<Coordenadas | Coordenadas[]> {
    let params = new HttpParams();
    if (id) {
      params = params.append('id', id.toString());
    }
    if (barrioId) {
      params = params.append('barrioId', barrioId.toString());
    }
    if (zonaId) {
      params = params.append('zonaId', zonaId.toString());
    }
    return this.http.get<Coordenadas | Coordenadas[]>(`${this.apiUrl}/find`, { params });
  }

  create(coordenadas: Coordenadas): Observable<Coordenadas> {
    return this.http.post<Coordenadas>(`${this.apiUrl}/create`, coordenadas);
  }

  update(id: number, coordenadas: Coordenadas): Observable<Coordenadas> {
    return this.http.put<Coordenadas>(`${this.apiUrl}/update?id=${id}`, coordenadas);
  }

  delete(id?: number, barrioId?: number, zonaId?: number): Observable<any> {
    let params = new HttpParams();
    if (id) {
      params = params.append('id', id.toString());
    }
    if (barrioId) {
      params = params.append('barrioId', barrioId.toString());
    }
    if (zonaId) {
      params = params.append('zonaId', zonaId.toString());
    }
    return this.http.delete(`${this.apiUrl}/delete`, { params });
  }
}
