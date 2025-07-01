import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
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
    return this.http.post<Campania>(this.apiUrl, campania).pipe(
      tap(() => alert('Campaña creada con éxito')),
      catchError(error => {
        alert('Error al crear la campaña');
        return throwError(error);
      })
    );
  }

  update(campania: Campania): Observable<Campania> {
    return this.http.put<Campania>(this.apiUrl, campania).pipe(
      tap(() => alert('Campaña actualizada con éxito')),
      catchError(error => {
        alert('Error al actualizar la campaña');
        return throwError(error);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => alert('Campaña eliminada con éxito')),
      catchError(error => {
        alert('Error al eliminar la campaña');
        return throwError(error);
      })
    );
  }
}
