import { Component, OnInit } from '@angular/core';
import { UsuarioService } from '../../services/usuario';
import { User } from '../../models/user.model';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-user-list',
  imports: [
        CommonModule,    // <-- Ensure CommonModule is here for *ngFor and *ngIf
    RouterModule     // For routerLink if used
  ],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css',
  standalone: true
})
export class UserList implements OnInit{
users: User[] = [];

  constructor(private usuarioService: UsuarioService, private router: Router) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.usuarioService.getUsers().subscribe(
      (data:any) => {
        this.users = data;
      },
      (error:any) => {
        console.error('Error fetching users:', error);
      }
    );
  }

  viewUserDetail(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/users/detail', id]);
    }
  }

  editUser(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/users/edit', id]);
    }
  }

  deleteUser(id: number | undefined): void {
    if (confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
      if (id) {
        this.usuarioService.deleteUser(id).subscribe(
          () => {
            console.log('User deleted successfully');
            this.loadUsers(); // Refresh the list
          },
          (error) => {
            console.error('Error deleting user:', error);
          }
        );
      }
    }
  }

  toggleUserStatus(user: User): void {
    if (user.id) {
      const newStatus = !user.activo; // Toggle the status
      this.usuarioService.toggleUserStatus(user.id, newStatus).subscribe(
        () => {
          user.activo = newStatus; // Update the local user object
          console.log(`User ${user.id} status toggled to ${newStatus}`);
        },
        (error : any) => {
          console.error('Error toggling user status:', error);
        }
      );
    }
  }

  goToNewUser(): void {
    this.router.navigate(['/users/new']);
  }
}
