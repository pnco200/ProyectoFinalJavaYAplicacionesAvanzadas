import { Routes } from '@angular/router';
import { UserList } from './components/user-list/user-list';
import { UserEdit } from './components/user-edit/user-edit';
import { UserDetail } from './components/user-detail/user-detail';
import { RegisterComponent } from './components/register/register';
import { AuthRoleGuard } from '../../services/auth-role.service';

export const USERS_ROUTES: Routes = [
  { path: 'list', component: UserList, canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'new', component: RegisterComponent, canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'edit/:id', component: UserEdit, canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'detail/:id', component: UserDetail, canActivate: [AuthRoleGuard], data: { roles: ['ADMINISTRADOR'] } },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];
