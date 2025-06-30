import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Encuestador } from '../models/encuestador.model';

@Injectable({
  providedIn: 'root'
})
export class EncuestadorService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/encuestador';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Encuestador[]> {
    return this.http.get<Encuestador[]>(`${this.apiUrl}/`);
  }

  getById(id: number): Observable<Encuestador> {
    return this.http.get<Encuestador>(`${this.apiUrl}/findById/${id}`);
  }

  getByDni(dni: string): Observable<Encuestador> {
    return this.http.get<Encuestador>(`${this.apiUrl}/findByDni/${dni}`);
  }

  create(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.post<Encuestador>(`${this.apiUrl}/create`, encuestador);
  }

  update(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.put<Encuestador>(`${this.apiUrl}/update`, encuestador);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
