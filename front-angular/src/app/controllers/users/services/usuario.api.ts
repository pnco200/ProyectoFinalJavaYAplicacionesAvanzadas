import { environment } from '../../../../environments/environment';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  add(usuario: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, usuario).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Usuario creado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  update(usuario: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${usuario.id}`, usuario).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Usuario actualizado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Usuario eliminado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  updateStatus(id: number, isActive: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado`, { is_active: isActive }).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Estado del usuario actualizado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  assignBarrioToUser(userId: number, barrioId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/barrio/${barrioId}`, {}, { responseType: 'text' }).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Barrio asignado con éxito.', 'success')),
      catchError(error => {
        Swal.fire('¡Error!', this.getErrorMessage(error), 'error');
        return throwError(error);
      })
    );
  }

  removeBarrioFromUser(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${userId}/barrio`).pipe(
      tap(() => Swal.fire('¡Éxito!', 'Barrio removido con éxito.', 'success')),
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