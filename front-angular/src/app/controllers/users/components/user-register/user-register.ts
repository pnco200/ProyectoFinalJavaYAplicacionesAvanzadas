import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User } from '../../models/user.model';
import { map, switchMap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.api';
import { Rol, RolService } from '../../../roles/services/rol.api';

@Component({
  selector: 'app-user-register',
  imports: [
    CommonModule,        
    ReactiveFormsModule, 
    FormsModule,        
    RouterModule
  ],
  standalone: true,
  templateUrl: './user-register.html'
})
export class UserRegister implements OnInit {
  userForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userForm = this.fb.group({
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      name: ['', Validators.required],
      is_active: [false],
      domicilio: ['', Validators.required],
      activity: ['', Validators.required],
      extra: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      const user: User = this.userForm.value;
      this.usuarioService.add(user).subscribe(() => {
        this.router.navigate(['/users/list']);
      });
    } else {
      console.log('Form is invalid');
    }
  }

  cancel(): void {
    this.router.navigate(['/users/list']);
  }
}
