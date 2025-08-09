
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/ProyectoFinal/api/usuarios';

  constructor(private http: HttpClient) { }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  assignBarrioToUser(userId: number, barrioId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/barrio/${barrioId}`, {}, { responseType: 'text' });
  }
}
