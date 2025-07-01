import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/usuarios';

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  add(usuario: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, usuario).pipe(
      tap(() => alert('Usuario creado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  update(usuario: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${usuario.id}`, usuario).pipe(
      tap(() => alert('Usuario actualizado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => alert('Usuario eliminado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  assignBarrioToUser(userId: number, barrioId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/barrio/${barrioId}`, {}, { responseType: 'text' }).pipe(
      tap(() => alert('Barrio asignado con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  removeBarrioFromUser(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${userId}/barrio`).pipe(
      tap(() => alert('Barrio removido con éxito')),
      catchError(error => {
        alert(this.getErrorMessage(error));
        return throwError(error);
      })
    );
  }

  private getErrorMessage(error: any): string {
    return error?.error?.error || 'Ocurrió un error inesperado.';
  }
}
