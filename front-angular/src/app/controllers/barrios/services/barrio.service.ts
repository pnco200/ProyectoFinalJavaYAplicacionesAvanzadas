import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Barrio } from '../models/barrio.model';

@Injectable({
  providedIn: 'root'
})
export class BarrioService {

  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/barrios';

  constructor(private http: HttpClient) { }

  getAll(id?: number): Observable<Barrio[]> {
    let params = new HttpParams();
    if (id) {
      params = params.append('id', id.toString());
    }
    return this.http.get<Barrio[]>(this.apiUrl, { params });
  }

  create(barrio: Barrio): Observable<Barrio> {
    return this.http.post<Barrio>(`${this.apiUrl}/create`, barrio);
  }

  update(id: number, barrio: Barrio): Observable<Barrio> {
    return this.http.put<Barrio>(`${this.apiUrl}/update/${id}`, barrio);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
