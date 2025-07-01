
import { Routes } from '@angular/router';
import { EncuestaListComponent } from './components/encuesta-list/encuesta-list.component';
import { EncuestaDetailComponent } from './components/encuesta-detail/encuesta-detail.component';
import { EncuestaFormComponent } from './components/encuesta-form/encuesta-form.component';

export const ENCUESTAS_ROUTES: Routes = [
  { path: '', component: EncuestaListComponent },
  { path: 'detail/:id', component: EncuestaDetailComponent },
  { path: 'create', component: EncuestaFormComponent },
  { path: 'edit/:id', component: EncuestaFormComponent },
];
