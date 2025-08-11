import { Routes } from '@angular/router';
import { BarrioListComponent } from './components/barrio-list/barrio-list.component';
import { BarrioFormComponent } from './components/barrio-form/barrio-form.component';
import { BarrioDetailComponent } from './components/barrio-detail/barrio-detail.component';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const BARRIOS_ROUTES: Routes = [
  { path: 'list', component: BarrioListComponent, 
      canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'new', component: BarrioFormComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'edit/:id', component: BarrioFormComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'detail/:id', component: BarrioDetailComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];