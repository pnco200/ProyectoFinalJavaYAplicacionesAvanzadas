import { Routes } from '@angular/router';
import { UserList } from './components/user-list/user-list';
import { UserEdit } from './components/user-edit/user-edit';
import { UserDetail } from './components/user-detail/user-detail';

export const USERS_ROUTES: Routes = [ // <-- Exporting an array directly
  { path: 'list', component: UserList },
  { path: 'new', component: UserEdit },
  { path: 'edit/:id', component: UserEdit },
  { path: 'detail/:id', component: UserDetail },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];
