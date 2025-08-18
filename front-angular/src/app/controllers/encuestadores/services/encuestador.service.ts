import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Encuestador } from '../models/encuestador';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class EncuestadorService {
  private apiUrl = `${environment.apiUrl}/encuestador`;

  constructor(private http: HttpClient) { }

  getEncuestadores(): Observable<Encuestador[]> {
    return this.http.get<Encuestador[]>(`${this.apiUrl}/`);
  }

  getEncuestador(id: number): Observable<Encuestador> {
    return this.http.get<Encuestador>(`${this.apiUrl}/findById/${id}`);
  }

  createEncuestador(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.post<Encuestador>(`${this.apiUrl}/create`, encuestador).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Encuestador creado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  updateEncuestador(encuestador: Encuestador): Observable<Encuestador> {
    return this.http.put<Encuestador>(`${this.apiUrl}/update`, encuestador).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Encuestador actualizado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  deleteEncuestador(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Encuestador eliminado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  private getErrorMessage(error: any): string {
    return error?.error?.error || 'Ocurrió un error inesperado.';
  }
}
