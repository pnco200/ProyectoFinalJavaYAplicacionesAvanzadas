import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Campania } from '../models/campania.model';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class CampaniaService {
  private apiUrl = `${environment.apiUrl}/campanias`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Campania[]> {
    return this.http.get<Campania[]>(this.apiUrl);
  }

  getByDateRange(startDate: string, endDate: string): Observable<Campania[]> {
    return this.http.get<Campania[]>(`${this.apiUrl}/by_date_range?startDate=${startDate}&endDate=${endDate}`);
  }

  getById(id: number): Observable<Campania> {
    return this.http.get<Campania>(`${this.apiUrl}/${id}`);
  }

  create(campania: Campania): Observable<Campania> {
    return this.http.post<Campania>(this.apiUrl, campania).pipe(
      tap(() => {
        Swal.fire({
          icon: 'success',
          title: 'Campaña creada con éxito',
          showConfirmButton: false,
          timer: 1500,
        });
      }),
      catchError(error => {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: '¡Algo salió mal!',
          footer: `<a href="">${this.getErrorMessage(error)}</a>`,
        });
        return throwError(error);
      })
    );
  }

  update(campania: Campania): Observable<Campania> {
    return this.http.put<Campania>(this.apiUrl, campania).pipe(
      tap(() => {
        Swal.fire({
          icon: 'success',
          title: 'Campaña actualizada con éxito',
          showConfirmButton: false,
          timer: 1500,
        });
      }),
      catchError(error => {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: '¡Algo salió mal!',
          footer: `<a href="">${this.getErrorMessage(error)}</a>`,
        });
        return throwError(error);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        Swal.fire({
          icon: 'success',
          title: 'Campaña eliminada con éxito',
          showConfirmButton: false,
          timer: 1500,
        });
      }),
      catchError(error => {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: '¡Algo salió mal!',
          footer: `<a href="">${this.getErrorMessage(error)}</a>`,
        });
        return throwError(error);
      })
    );
  }

  uploadEncuestas(id: number, data: any): Observable<any> {
    Swal.fire({
      title: 'Espere por favor',
      didOpen: () => {
        Swal.showLoading();
      },
    });

    return this.http.post(`${this.apiUrl}/${id}/upload-encuestas`, data).pipe(
      tap(() => {
        Swal.fire({
          icon: 'success',
          title: 'Encuestas subidas con éxito',
          showConfirmButton: false,
          timer: 1500,
        });
      }),
      catchError(error => {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: '¡Algo salió mal!',
          footer: `<a href="">${this.getErrorMessage(error)}</a>`,
        });
        return throwError(error);
      })
    );
  }

  private getErrorMessage(error: any): string {
    return error?.error?.error || 'Ocurrió un error inesperado.';
  }
}
