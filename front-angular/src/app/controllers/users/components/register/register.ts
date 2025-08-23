import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { RolService } from '../../../roles/services/rol.service';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { User } from '../../models/user.model';
import { Rol } from '../../../roles/models/rol.model';
import { Barrio } from '../../../barrios/models/barrio.model';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  roles: Rol[] = [];
  barrios: Barrio[] = [];
  showBarrios = false;
  isAdmin = false;
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private rolService: RolService,
    private barrioService: BarrioService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      name: ['', Validators.required],
      domicilio: ['', Validators.required],
      activity: ['', Validators.required],
      extra: [''],
      rolId: ['', Validators.required],
      barrioId: ['']
    });
  }

  ngOnInit(): void {
    const userStr = localStorage.getItem('currentUser');
    const user = JSON.parse(userStr || '{}');
    if (user && user.roles && user.roles.includes('ADMINISTRADOR')) {
      this.isAdmin = true;
    }
    this.rolService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles.filter(rol => rol.nombre !== 'ADMINISTRADOR');
      },
      error: (err) => {
        console.error('Error fetching roles:', err);
        // Optionally, you could display a user-friendly message here
      }
    });

    this.registerForm.get('rolId')?.valueChanges.subscribe(rolId => {
      const selectedRol = this.roles.find(rol => rol.id === +rolId);
      if (selectedRol && selectedRol.nombre === 'ORGANIZACION_SOCIAL') {
        this.showBarrios = true;
        this.barrioService.getBarrios().subscribe(barrios => {
          this.barrios = barrios;
        });
      } else {
        this.showBarrios = false;
        this.barrios = [];
      }
    });
  }

  onSubmit(): void {
    console.log(this.isAdmin);
    if (this.registerForm.valid) {
      const newUser: User = { ...this.registerForm.value };
      if (!this.showBarrios) {
        delete newUser.barrioId;
      }
      if(this.isAdmin){
        newUser.is_active = true;
      }
      this.userService.createUser(newUser).subscribe({
        next: createdUser => {
          if (this.showBarrios && this.registerForm.value.barrioId) {
            this.userService.assignBarrioToUser(createdUser.id, this.registerForm.value.barrioId).subscribe({
              next: () => {
                Swal.fire('¡Éxito!', 'Usuario registrado y barrio asignado exitosamente!', 'success');
                this.router.navigate(['/login']);
              },
              error: (err) => {
                Swal.fire('¡Error!', 'Usuario registrado, pero hubo un error al asignar el barrio.', 'error');
                console.error('Error assigning barrio:', err);
                this.router.navigate(['/login']);
              }
            });
          } else {
            Swal.fire('¡Éxito!', 'Usuario registrado exitosamente!', 'success');
            this.router.navigate(['/login']);
          }
        },
        error: (err) => {
          Swal.fire('¡Error!', 'Error al registrar usuario: ' + (err.error || err.message), 'error');
          console.error('Error registering user:', err);
        }
      });
    }
  }
}