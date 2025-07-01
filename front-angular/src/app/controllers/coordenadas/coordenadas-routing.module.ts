import { Routes } from '@angular/router';
import { CoordenadasListComponent } from './components/coordenadas-list/coordenadas-list.component';
import { CoordenadasFormComponent } from './components/coordenadas-form/coordenadas-form.component';
import { CoordenadasDetailComponent } from './components/coordenadas-detail/coordenadas-detail.component';

export const COORDENADAS_ROUTES: Routes = [
  { path: 'list', component: CoordenadasListComponent },
  { path: 'new', component: CoordenadasFormComponent },
  { path: 'edit/:id', component: CoordenadasFormComponent },
  { path: 'detail/:id', component: CoordenadasDetailComponent },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];