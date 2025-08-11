import { Routes } from '@angular/router';
import { EncuestadoresComponent } from './components/encuestadores.component';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const ENCUESTADORES_ROUTES: Routes = [
  { path: '', component: EncuestadoresComponent,
     canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } }
];
