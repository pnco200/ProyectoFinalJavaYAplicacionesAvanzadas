import { Routes } from '@angular/router';
import { RenglonListComponent } from './components/renglon-list/renglon-list.component';
import { RenglonFormComponent } from './components/renglon-form/renglon-form.component';
import { RenglonDetailComponent } from './components/renglon-detail/renglon-detail.component';

export const RENGLONES_ROUTES: Routes = [
  { path: 'list', component: RenglonListComponent },
  { path: 'new', component: RenglonFormComponent },
  { path: 'edit/:id', component: RenglonFormComponent },
  { path: 'detail/:id', component: RenglonDetailComponent },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];