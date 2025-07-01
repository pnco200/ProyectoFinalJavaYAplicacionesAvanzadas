import { Routes } from '@angular/router';
import { BarrioListComponent } from './components/barrio-list/barrio-list.component';
import { BarrioFormComponent } from './components/barrio-form/barrio-form.component';
import { BarrioDetailComponent } from './components/barrio-detail/barrio-detail.component';

export const BARRIOS_ROUTES: Routes = [
  { path: 'list', component: BarrioListComponent },
  { path: 'new', component: BarrioFormComponent },
  { path: 'edit/:id', component: BarrioFormComponent },
  { path: 'detail/:id', component: BarrioDetailComponent },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];