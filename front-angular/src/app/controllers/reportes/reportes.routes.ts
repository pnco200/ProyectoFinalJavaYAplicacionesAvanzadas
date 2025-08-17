
import { Routes } from '@angular/router';
import { SubirReporteComponent } from './components/subir-reporte/subir-reporte';
import { AuthRoleGuard } from '../../services/auth-role.service';
import { SolicitarReporteComponent } from '../../components/solicitar-reporte/solicitar-reporte.component';


export const REPORTES_ROUTES: Routes = [
  {
    path: 'subir',
    component: SubirReporteComponent,
    canActivate: [AuthRoleGuard],
    data: { roles: ['PERSONAL_SALUD'] }
  },
  {
    path: 'solicitar',
    component: SolicitarReporteComponent,
    canActivate: [AuthRoleGuard],
    data: { roles: ['ORGANIZACION_SOCIAL'] }
  },
  {
    path: '',
    redirectTo: 'subir',
    pathMatch: 'full'
  }
];
