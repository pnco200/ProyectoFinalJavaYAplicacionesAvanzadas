import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Campania } from '../models/campania.model';

@Injectable({
  providedIn: 'root'
})
export class CampaniaService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/campanias';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Campania[]> {
    return this.http.get<Campania[]>(this.apiUrl);
  }

  getById(id: number): Observable<Campania> {
    return this.http.get<Campania>(`${this.apiUrl}/${id}`);
  }

  create(campania: Campania): Observable<Campania> {
    return this.http.post<Campania>(this.apiUrl, campania);
  }

  update(campania: Campania): Observable<Campania> {
    // The backend PUT endpoint seems to expect the full object, not an ID in the URL
    return this.http.put<Campania>(this.apiUrl, campania);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
