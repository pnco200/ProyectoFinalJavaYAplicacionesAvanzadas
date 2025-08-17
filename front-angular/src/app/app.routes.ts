import { Routes } from '@angular/router';
import { HomePage } from './views/home-page/home-page';
import { LoginComponent } from './controllers/users/components/login/login.component';

import { RenglonesListComponent } from './controllers/renglones/components/renglones-list/renglones-list.component';
import { RenglonesRespuestasComponent } from './controllers/renglones/components/renglones-respuestas/renglones-respuestas.component';
import { InactiveUser } from './components/inactive-user/inactive-user';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'login', component: LoginComponent },
  { path: 'inactive', component: InactiveUser },
  {
    path: 'users',
    loadChildren: () => import('./controllers/users/users-routing-module').then(m => m.USERS_ROUTES)
  },
  {
    path: 'campanias',
    loadChildren: () => import('./controllers/campanias/campanias.routes').then(m => m.CAMPANIAS_ROUTES)
  },
  //{
  //  path: 'coordenadas',
  //  loadChildren: () => import('./controllers/coordenadas/coordenadas-routing.module').then(m => m.COORDENADAS_ROUTES)
  //},
  {
    path: 'barrios',
    loadChildren: () => import('./controllers/barrios/barrios-routing.module').then(m => m.BARRIOS_ROUTES)
  },
  {
    path: 'zonas',
    loadChildren: () => import('./controllers/zonas/zonas-routing.module').then(m => m.ZONAS_ROUTES)
  },
  {
    path: 'renglones',
    children: [
      { path: 'list', component: RenglonesListComponent },
      { path: 'respuestas', component: RenglonesRespuestasComponent }
    ]
  },
  //{
  //  path: 'encuestas',
  //  loadChildren: () => import('./controllers/encuestas/encuestas.routes').then(m => m.ENCUESTAS_ROUTES)
  //},
  {
    path: 'jornadas',
    loadChildren: () => import('./controllers/jornadas/jornadas.routes').then(m => m.JORNADAS_ROUTES)
  },
  {
    path: 'encuestadores',
    loadChildren: () => import('./controllers/encuestadores/encuestadores.routes').then(m => m.ENCUESTADORES_ROUTES)
  },
  {
    path: 'reportes',
    loadChildren: () => import('./controllers/reportes/reportes.routes').then(m => m.REPORTES_ROUTES)
  },
  { path: '**', redirectTo: '', pathMatch: 'full' } // Wildcard route for any unmatched paths
];