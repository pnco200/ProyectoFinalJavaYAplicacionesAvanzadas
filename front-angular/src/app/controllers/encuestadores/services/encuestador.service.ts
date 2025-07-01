import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Encuestador } from '../models/encuestador';

@Injectable({
  providedIn: 'root'
})
export class EncuestadorService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/encuestador';

  constructor(private http: HttpClient) { }

  getEncuestadores(): Observable<Encuestador[]> {
    return this.http.get<Encuestador[]>(`${this.apiUrl}/`);
  }

  getEncuestador(id: number): Observable<Encuestador> {
    return this.http.get<Encuestador>(`${this.apiUrl}/findById/${id}`);
  }

  createEncuestador(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.post<Encuestador>(`${this.apiUrl}/create`, encuestador);
  }

  updateEncuestador(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.put<Encuestador>(`${this.apiUrl}/update`, encuestador);
  }

  deleteEncuestador(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
