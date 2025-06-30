import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Jornada } from '../models/jornada.model';

@Injectable({
  providedIn: 'root'
})
export class JornadaService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/jornadas';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Jornada[]> {
    return this.http.get<Jornada[]>(this.apiUrl);
  }

  getById(id: number): Observable<Jornada> {
    return this.http.get<Jornada>(`${this.apiUrl}/${id}`);
  }

  create(jornada: Jornada): Observable<Jornada> {
    return this.http.post<Jornada>(this.apiUrl, jornada);
  }

  update(jornada: Jornada): Observable<Jornada> {
    return this.http.put<Jornada>(this.apiUrl, jornada);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  addEncuestador(jornadaId: number, encuestadorId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${jornadaId}/encuestadores/${encuestadorId}`, null);
  }

  removeEncuestador(jornadaId: number, encuestadorId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${jornadaId}/encuestadores/${encuestadorId}`);
  }
}
