import { Routes } from '@angular/router';
import { HomePage } from './views/home-page/home-page';


export const routes: Routes = [
     { path: '', component: HomePage }, // Página de inicio

  // Carga perezosa para tus características (ahora directamente a un archivo de rutas)
  {
    path: 'users',
    loadChildren: () => import('./controllers/users/users-routing-module').then(m => m.USERS_ROUTES)
  }, // { path: 'campaigns', loadChildren: () => import('./campaigns/campaigns.routes').then(m => m.CAMPAIGNS_ROUTES) },
  //{ path: 'journeys', loadChildren: () => import('./journeys/journeys.routes').then(m => m.JOURNEYS_ROUTES) },
];
