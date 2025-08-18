import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Renglon } from '../models/renglon.model';

@Injectable({
  providedIn: 'root'
})
export class RenglonService {

  private apiUrl = `${environment.apiUrl}/renglon`;

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

  getTipos(): Observable<[string,string][]> {
    return this.http.get<[string,string][]>(`${this.apiUrl}/tipos`);
  }

  getChartData(encuestaIds: number[]): Observable<any[]> {
    const url = `${this.apiUrl}/chart-data`;
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<any[]>(url, encuestaIds, { headers });
  }

  getValores(tipo: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/valores/${tipo}`);
  }

  filtrarRenglones(filtro: any): Observable<any[]> {
    return this.http.post<any[]>(`${this.apiUrl}/filtrar`, filtro);
  }

  getRespuestas(encuestaId: number): Observable<any[]> {

    return this.http.get<any[]>(`${this.apiUrl}/respuestas/${encuestaId}`);
  }
}
