// auth-role.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthRoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const userStr = localStorage.getItem('currentUser');
    if (!userStr) {
      this.router.navigate(['/login']);
      return false;
    }

    try {
      const user = JSON.parse(userStr);
      const allowedRoles = route.data['roles'] as string[] || [];
      if (user.is_active == false) {
        this.router.navigate(['/inactive']);
        return false;
      }
      if (user.roles && allowedRoles.some(role => user.roles.includes(role))) {
        return true;
      }
    } catch (e) {
      console.error('Error parsing user data', e);
    }

    this.router.navigate(['/login']);
    return false;
  }
}
