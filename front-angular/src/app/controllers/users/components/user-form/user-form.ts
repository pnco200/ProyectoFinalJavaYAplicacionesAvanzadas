import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User } from '../../models/user.model';
import { map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.api';

@Component({
  selector: 'app-user-form',
  imports: [
    CommonModule,        // <-- Ensure CommonModule is here for *ngIf
    ReactiveFormsModule, // <-- Ensure ReactiveFormsModule is here for [formGroup]
    FormsModule,         // (Optional, if you use ngModel directly, otherwise ReactiveFormsModule is enough)
    RouterModule
  ],
  standalone: true,
  templateUrl: './user-form.html',
  styleUrl: './user-form.css'
})
export class UserForm implements OnInit {
  userForm!: FormGroup;
  isEditMode: boolean = false;
  userId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.userForm = this.fb.group({
      dni: ['', [Validators.required, Validators.pattern(/^\d{7,8}$/)]], // Example DNI validation
      apellido: ['', Validators.required],
      nombres: ['', Validators.required],
      domicilio: ['', Validators.required],
      genero: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      activo: [true] // Default to active for new users
    });

    this.route.paramMap.pipe(
      map(params => params.get('id')),
      switchMap(id => {
        if (id) {
          this.isEditMode = true;
          this.userId = +id;
          return this.usuarioService.getById(this.userId);
        } else {
          return of(null);
        }
      })
    ).subscribe(user => {
      if (user) {
        this.userForm.patchValue(user);
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      const user: User = this.userForm.value;
      if (this.isEditMode && this.userId) {
        this.usuarioService.update(user).subscribe(() => {
          this.router.navigate(['/users/list']);
        });
      } else {
        this.usuarioService.add(user).subscribe(() => {
          this.router.navigate(['/users/list']);
        });
      }
    } else {
      console.log('Form is invalid');
    }
  }

  cancel(): void {
    this.router.navigate(['/users/list']);
  }
}
