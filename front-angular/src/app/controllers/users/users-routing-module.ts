import { Routes } from '@angular/router';
import { UserList } from './components/user-list/user-list';
import { UserForm } from './components/user-form/user-form';
import { UserDetail } from './components/user-detail/user-detail';

export const USERS_ROUTES: Routes = [ // <-- Exporting an array directly
  { path: 'list', component: UserList },
  { path: 'new', component: UserForm },
  { path: 'edit/:id', component: UserForm },
  { path: 'detail/:id', component: UserDetail },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];
