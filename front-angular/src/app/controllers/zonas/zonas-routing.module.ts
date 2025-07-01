import { Routes } from '@angular/router';
import { ZonaListComponent } from './components/zona-list/zona-list.component';
import { ZonaFormComponent } from './components/zona-form/zona-form.component';
import { ZonaDetailComponent } from './components/zona-detail/zona-detail.component';

export const ZONAS_ROUTES: Routes = [
  { path: 'list', component: ZonaListComponent },
  { path: 'new', component: ZonaFormComponent },
  { path: 'edit/:id', component: ZonaFormComponent },
  { path: 'detail/:id', component: ZonaDetailComponent },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];