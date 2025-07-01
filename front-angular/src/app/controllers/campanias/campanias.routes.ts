// campanias.routes.ts o campanias-routing.ts

import { Routes } from '@angular/router';
import { CampaniaListComponent } from './components/campania-list/campania-list.component';
import { CampaniaFormComponent } from './components/campania-form/campania-form.component';
import { CampaniaDetailComponent } from './components/campania-detail/campania-detail.component';

export const CAMPANIAS_ROUTES: Routes = [
  {
    path: '',
    component: CampaniaListComponent
  },
  {
    path: 'nuevo',
    component: CampaniaFormComponent
  },
  {
    path: ':id',
    component: CampaniaDetailComponent
  },
  {
    path: ':id/editar',
    component: CampaniaFormComponent
  }
];
