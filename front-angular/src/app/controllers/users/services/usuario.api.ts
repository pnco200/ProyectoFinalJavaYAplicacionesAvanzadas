import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs'; // Importa 'of' para crear Observables simples
import { delay } from 'rxjs/operators'; // Importa 'delay' para simular latencia de red
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  add(usuario: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, usuario);
  }

  update(usuario: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${usuario.id}`, usuario);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  toggleHabilitado(id: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/toggle`, {});
  }
}