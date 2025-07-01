import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export interface Rol {
  id: number;
  nombre: string;
}

@Injectable({
  providedIn: 'root',
})
export class RolService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/roles';

  constructor(private http: HttpClient) {}

  getAllRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(this.apiUrl).pipe(
      tap(roles => console.log('Roles obtenidos (getAllRoles):', roles)),
      catchError(error => {
        console.error('Error al obtener roles (getAllRoles):', error);
        return throwError(error);
      })
    );
  }

  assignRoleToUser(rolId: number, userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${rolId}/usuarios/${userId}`, {});
  }

  removeRoleFromUser(rolId: number, userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${rolId}/usuarios/${userId}`);
  }
}