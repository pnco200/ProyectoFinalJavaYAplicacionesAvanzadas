// campanias.routes.ts o campanias-routing.ts

import { Routes } from '@angular/router';
import { CampaniaListComponent } from './components/campania-list/campania-list.component';
import { CampaniaFormComponent } from './components/campania-form/campania-form.component';
import { CampaniaDetailComponent } from './components/campania-detail/campania-detail.component';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const CAMPANIAS_ROUTES: Routes = [
  {
    path: '',
    component: CampaniaListComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR', "PERSONAL_SALUD"] }
  },
  {
    path: 'nuevo',
    component: CampaniaFormComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] }
  },
  {
    path: ':id',
    component: CampaniaDetailComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR', "PERSONAL_SALUD"] }
  },
  {
    path: ':id/editar',
    component: CampaniaFormComponent, 
    canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] }
  }
];
