import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Jornada } from '../models/jornada';
import { Encuestador } from '../../encuestadores/models/encuestador';

@Injectable({
  providedIn: 'root'
})
export class JornadaService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/jornadas';

  constructor(private http: HttpClient) { }

  getJornadas(): Observable<Jornada[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(response => response.map(item => this.mapToJornada(item)))
    );
  }

  getJornada(id: number): Observable<Jornada> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => this.mapToJornada(response))
    );
  }

  createJornada(jornada: Jornada): Observable<Jornada> {
    const payload = this.mapToPayload(jornada);
    return this.http.post<any>(this.apiUrl, payload).pipe(
      map(response => this.mapToJornada(response)),
      tap(() => alert('Jornada creada con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  updateJornada(jornada: Jornada): Observable<Jornada> {
    const payload = this.mapToPayload(jornada);
    return this.http.put<any>(`${this.apiUrl}`, payload).pipe(
      map(response => this.mapToJornada(response)),
      tap(() => alert('Jornada actualizada con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  deleteJornada(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => alert('Jornada eliminada con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  getEncuestadores(jornadaId: number): Observable<Encuestador[]> {
    return this.http.get<Encuestador[]>(`${this.apiUrl}/${jornadaId}/encuestadores`);
  }

  addEncuestador(jornadaId: number, encuestadorId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${jornadaId}/encuestadores/${encuestadorId}`, null).pipe(
      tap(() => alert('Encuestador agregado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  removeEncuestador(jornadaId: number, encuestadorId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${jornadaId}/encuestadores/${encuestadorId}`).pipe(
      tap(() => alert('Encuestador eliminado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  private mapToJornada(item: any): Jornada {
    return {
      id: item.id,
      name: item.name,
      startDate: item.startDate,
      endDate: item.endDate,
      campaniaId: item.campañaId
    };
  }

  private mapToPayload(jornada: Jornada): any {
    return {
      id: jornada.id,
      name: jornada.name,
      startDate: jornada.startDate,
      endDate: jornada.endDate,
      campañaId: jornada.campaniaId
    };
  }

  private getErrorMessage(error: any): string {
    return error?.error?.error || 'Ocurrió un error inesperado.';
  }
}
