import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Zona } from '../models/zona.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ZonaService {

  private apiUrl = `${environment.apiUrl}/zonas`;

  constructor(private http: HttpClient) { }

  findById(id: number): Observable<Zona[]> {
    return this.http.get<Zona[]>(`${this.apiUrl}/findById/${id}`);
  }

  findByBarrioId(barrioId: number): Observable<Zona[]> {
    return this.http.get<Zona[]>(`${this.apiUrl}/findByBarrioId/${barrioId}`);
  }

  findAll(): Observable<Zona[]> {
    return this.http.get<Zona[]>(`${this.apiUrl}/all`);
  }

  create(zona: Zona): Observable<Zona> {
    return this.http.post<Zona>(`${this.apiUrl}/create`, zona);
  }

  update(id: number, zona: Zona): Observable<Zona> {
    return this.http.put<Zona>(`${this.apiUrl}/update/${id}`, zona);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
