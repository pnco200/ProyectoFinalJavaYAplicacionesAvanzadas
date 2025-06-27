import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs'; // Importa 'of' para crear Observables simples
import { delay } from 'rxjs/operators'; // Importa 'delay' para simular latencia de red
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  // private apiUrl = 'YOUR_API_BASE_URL/users'; // Descomenta esto cuando uses la API real

  // --- DATOS SIMULADOS (HARDCODEADOS) ---
  private users: User[] = [
    { id: 1, dni: '12345678', apellido: 'Perez', nombres: 'Juan Carlos', domicilio: 'Calle Falsa 123', genero: 'Masculino', email: 'juan.perez@example.com', activo: true },
    { id: 2, dni: '87654321', apellido: 'Gomez', nombres: 'Ana Laura', domicilio: 'Av. Siempreviva 742', genero: 'Femenino', email: 'ana.gomez@example.com', activo: true },
    { id: 3, dni: '11223344', apellido: 'Rodriguez', nombres: 'Luis Alberto', domicilio: 'Paseo del Bosque s/n', genero: 'Masculino', email: 'luis.rodriguez@example.com', activo: false },
    { id: 4, dni: '55667788', apellido: 'Fernandez', nombres: 'María Soledad', domicilio: 'Diagonal 80 Nro 500', genero: 'Femenino', email: 'maria.fernandez@example.com', activo: true }
  ];
  private nextId: number = 5; // Para simular la asignación de IDs en nuevos usuarios

  constructor(/* private http: HttpClient */) {
    // No necesitas HttpClient en el constructor mientras simulas los datos
  }

  getUsers(): Observable<User[]> {
    console.log('Simulando getUsers()');
    return of([...this.users]).pipe(delay(500)); // Retorna una copia para evitar mutaciones directas
  }

  getUserById(id: number): Observable<User | undefined> { // Podría no encontrarlo, por eso 'undefined'
    console.log(`Simulando getUserById(${id})`);
    const user = this.users.find(u => u.id === id);
    return of(user).pipe(delay(500));
  }

  createUser(user: User): Observable<User> {
    console.log('Simulando createUser()', user);
    const newUser: User = { ...user, id: this.nextId++ };
    this.users.push(newUser);
    return of(newUser).pipe(delay(500));
  }

  updateUser(id: number, updatedUser: User): Observable<User> {
    console.log(`Simulando updateUser(${id})`, updatedUser);
    const index = this.users.findIndex(u => u.id === id);
    if (index > -1) {
      this.users[index] = { ...updatedUser, id: id }; // Asegura que el ID no cambie
      return of(this.users[index]).pipe(delay(500));
    }
    // Si no lo encuentra, podrías lanzar un error o devolver un Observable de error
    return of(updatedUser).pipe(delay(500)); // Para simplificar, devolvemos el usuario actualizado
  }

  deleteUser(id: number): Observable<void> {
    console.log(`Simulando deleteUser(${id})`);
    this.users = this.users.filter(u => u.id !== id);
    return of(void 0).pipe(delay(500)); // void 0 es equivalente a undefined, pero más explícito
  }

  toggleUserStatus(id: number, enable: boolean): Observable<User> {
    console.log(`Simulando toggleUserStatus(${id}), nuevo estado: ${enable}`);
    const user = this.users.find(u => u.id === id);
    if (user) {
      user.activo = enable;
      return of(user).pipe(delay(500));
    }
    // Manejar caso donde el usuario no se encuentra
    return of({} as User).pipe(delay(500)); // Devuelve un usuario vacío o maneja el error
  }

  // --- FIN DE DATOS SIMULADOS ---
}