import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { UsuarioService } from '../../services/usuario';
import { User } from '../../models/user.model';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [
        CommonModule,    // <-- Ensure CommonModule is here for *ngFor and *ngIf
    RouterModule     // For routerLink if used
  ],
  templateUrl: './user-detail.html',
  styleUrl: './user-detail.css'
})
export class UserDetail implements OnInit {
  user: User | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: UsuarioService
  ) { }

  ngOnInit(): void {
    const userId = this.route.snapshot.paramMap.get('id');
    if (userId) {
      this.usuarioService.getUserById(+userId).subscribe(
        (data) => {
          this.user = data;
        },
        (error) => {
          console.error('Error fetching user detail:', error);
          this.router.navigate(['/users/list']); // Redirect if user not found
        }
      );
    } else {
      this.router.navigate(['/users/list']);
    }
  }

  goBack(): void {
    this.router.navigate(['/users/list']);
  }
}
