import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User } from '../../models/user.model';
import { map, switchMap, tap, take } from 'rxjs/operators';
import { of, Observable, forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.api';
import { Rol, RolService } from '../../../roles/services/rol.api';
import { Barrio } from '../../../barrios/models/barrio.model';
import { BarrioService } from '../../../barrios/services/barrio.service';

@Component({
  selector: 'app-user-edit',
  imports: [
    CommonModule,        
    ReactiveFormsModule, 
    FormsModule,         
    RouterModule
  ],
  standalone: true,
  templateUrl: './user-edit.html'
})
export class UserEdit implements OnInit {
  userForm!: FormGroup;
  userId: number | null = null;
  availableRoles: Rol[] = [];
  userCurrentRoles: Rol[] = [];
  roleToAdd: number | null = null;
  roleToRemove: number | null = null;
  isEditMode: boolean = false;
  availableBarrios: Barrio[] = [];
  userCurrentBarrio: Barrio | null = null;
  barrioToAssign: number | null = null;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private rolService: RolService,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.userForm = this.fb.group({
      id: [null],
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      name: ['', Validators.required],
      is_active: [false],
      domicilio: [''],
      activity: [''],
      extra: ['']
    });

    forkJoin({
      roles: this.rolService.getAllRoles(),
      barrios: this.barrioService.getAll(),
      userData: this.route.paramMap.pipe(
        take(1),
        map(params => params.get('id')),
        switchMap((id): Observable<User | null> => {
          console.log('ID de la ruta:', id);
          if (id) {
            this.userId = +id;
            this.isEditMode = true;
            return this.usuarioService.getById(this.userId).pipe(
              tap((user: User) => console.log('Usuario obtenido por getById:', user))
            );
          } else {
            return of(null);
          }
        })
      )
    }).subscribe(({ roles, barrios, userData }: { roles: Rol[], barrios: Barrio[], userData: User | null }) => {
      this.availableRoles = roles;
      this.availableBarrios = barrios;

      console.log('UserData recibido en forkJoin:', userData);
      if (userData) {
        this.userForm.patchValue(userData);
        this.loadUserRoles(userData); // Pasa userdata directamente

        if (this.hasOrganizaconSocialRole()) {
          this.userCurrentBarrio = this.availableBarrios.find(b => b.name === userData.barrio) || null;
        }
      }

      
      if (this.isEditMode) {
        this.userForm.get('password')?.clearValidators();
      } else {
        this.userForm.get('password')?.setValidators(Validators.required);
      }
      this.userForm.get('password')?.updateValueAndValidity();
    });
  }

  hasOrganizaconSocialRole(): boolean {
    return this.userCurrentRoles.some(role => role.nombre === 'ORGANIZACION_SOCIAL');
  }

  assignBarrio(): void {
    if (this.userId && this.barrioToAssign) {
      this.usuarioService.assignBarrioToUser(this.userId, this.barrioToAssign).subscribe(() => {
        
        this.userCurrentBarrio = this.availableBarrios.find(b => b.id === this.barrioToAssign) || null;
        this.barrioToAssign = null; // Resetea dropdown
      }, error => {
        console.error('Error assigning barrio:', error);
      });
    }
  }

  removeBarrio(): void {
    if (this.userId && this.userCurrentBarrio) {
      this.usuarioService.removeBarrioFromUser(this.userId).subscribe(() => {
        this.userCurrentBarrio = null; 
      }, error => {
        console.error('Error removing barrio:', error);
      });
    }
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      const user: User = this.userForm.value;

      
      if (this.isEditMode && !user.password) {
        delete user.password;
      }

      
      if (this.isEditMode) {
        this.usuarioService.update(user).subscribe(() => {
          this.router.navigate(['/users/list']);
        }, error => {
          console.error('Error updating user:', error);
        });
      } else {
        this.usuarioService.add(user).subscribe(() => {
          this.router.navigate(['/users/list']);
        }, error => {
          console.error('Error creating user:', error);
        });
      }
    } else {
      console.log('Form is invalid');
      
      this.userForm.markAllAsTouched();
    }
  }

  loadUserRoles(user: User): void {
    if (user && user.roles) {
      this.userCurrentRoles = this.availableRoles.filter(role => user.roles?.includes(role.nombre));
    }
  }

  getRolesToAdd(): Rol[] {
    return this.availableRoles.filter(role => !this.userCurrentRoles.some(ur => ur.id === role.id));
  }

  getRolesToRemove(): Rol[] {
    return this.userCurrentRoles;
  }

  addRoleToUser(): void {
    if (this.userId && this.roleToAdd) {
      this.rolService.assignRoleToUser(this.roleToAdd, this.userId).subscribe(() => {
        this.usuarioService.getById(this.userId!).subscribe(user => {
          this.loadUserRoles(user);
        });
        this.roleToAdd = null; // Resetea el dropdown
      }, error => {
        console.error('Error assigning role:', error);
      });
    }
  }

  removeRoleFromUser(): void {
    if (this.userId && this.roleToRemove) {
      this.rolService.removeRoleFromUser(this.roleToRemove, this.userId).subscribe(() => {
        this.usuarioService.getById(this.userId!).subscribe(user => {
          this.loadUserRoles(user);
        });
        this.roleToRemove = null; // Resetea el dropdown
      }, error => {
        console.error('Error removing role:', error);
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/users/list']);
  }
}
