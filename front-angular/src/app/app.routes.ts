import { Routes } from '@angular/router';
import { AdminHome } from './admin-home/admin-home';
import { HomePage } from './home-page/home-page';
import { UserRegistration } from './user-registration/user-registration';


export const routes: Routes = [
     { path: '', component: HomePage }, // Página de inicio
  { path: 'home/admin', component: AdminHome }, // Home del perfil administrador
  { path: 'register-user', component: UserRegistration }, // Enlace a formulario de nuevo usuario

  // Carga perezosa para tus características (ahora directamente a un archivo de rutas)
  {
    path: 'users',
    loadChildren: () => import('./users/users-routing-module').then(m => m.USERS_ROUTES)
  },
  {
    path: 'campanias',
    loadChildren: () => import('./campanias/campanias-routing.module').then(m => m.CampaniasRoutingModule)
  },
  //{ path: 'journeys', loadChildren: () => import('./journeys/journeys.routes').then(m => m.JOURNEYS_ROUTES) },
];
