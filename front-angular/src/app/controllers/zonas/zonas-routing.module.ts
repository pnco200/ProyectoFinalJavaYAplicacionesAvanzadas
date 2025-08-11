import { Routes } from '@angular/router';
import { ZonaListComponent } from './components/zona-list/zona-list.component';
import { ZonaFormComponent } from './components/zona-form/zona-form.component';
import { ZonaDetailComponent } from './components/zona-detail/zona-detail.component';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const ZONAS_ROUTES: Routes = [
  { path: 'list', component: ZonaListComponent , 
        canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'new', component: ZonaFormComponent,
     canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] }},
  { path: 'edit/:id', component: ZonaFormComponent,
     canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'detail/:id', component: ZonaDetailComponent,
     canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];