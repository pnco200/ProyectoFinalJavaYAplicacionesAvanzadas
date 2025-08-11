import { Routes } from '@angular/router';
import { JornadasComponent } from './components/jornadas.component';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const JORNADAS_ROUTES: Routes = [
  { path: '', component: JornadasComponent ,
     canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } }
];
