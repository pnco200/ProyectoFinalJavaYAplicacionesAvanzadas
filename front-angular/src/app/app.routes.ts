import { Routes } from '@angular/router';
import { HomePage } from './views/home-page/home-page';
import { LoginComponent } from './controllers/users/components/login/login.component';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'login', component: LoginComponent },
  {
    path: 'users',
    loadChildren: () => import('./controllers/users/users-routing-module').then(m => m.USERS_ROUTES)
  },
  {
    path: 'campanias',
    loadChildren: () => import('./controllers/campanias/campanias.routes').then(m => m.CAMPANIAS_ROUTES)
  },
  {
    path: 'coordenadas',
    loadChildren: () => import('./controllers/coordenadas/coordenadas-routing.module').then(m => m.COORDENADAS_ROUTES)
  },
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
    loadChildren: () => import('./controllers/renglones/renglones-routing.module').then(m => m.RENGLONES_ROUTES)
  },
  {
    path: 'encuestas',
    loadChildren: () => import('./controllers/encuestas/encuestas.routes').then(m => m.ENCUESTAS_ROUTES)
  },
  {
    path: 'jornadas',
    loadChildren: () => import('./controllers/jornadas/jornadas.routes').then(m => m.JORNADAS_ROUTES)
  },
  {
    path: 'encuestadores',
    loadChildren: () => import('./controllers/encuestadores/encuestadores.routes').then(m => m.ENCUESTADORES_ROUTES)
  },
  { path: '**', redirectTo: '', pathMatch: 'full' } // Wildcard route for any unmatched paths
];;