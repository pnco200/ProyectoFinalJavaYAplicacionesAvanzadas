
import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Barrio } from '../models/barrio.model';

@Injectable({
  providedIn: 'root'
})
export class BarrioService {
  private apiUrl = `${environment.apiUrl}/barrios`;

  constructor(private http: HttpClient) { }

  getBarrios(): Observable<Barrio[]> {
    return this.http.get<Barrio[]>(this.apiUrl);
  }

  getAll(id?: number): Observable<Barrio[]> {
    const url = id ? `${this.apiUrl}?id=${id}` : this.apiUrl;
    return this.http.get<Barrio[]>(url);
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
