import { Routes } from '@angular/router';
import { TrackedProductsComponent } from './pages/tracked-products/tracked-products.component';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard } from './core/guards/auth.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [noAuthGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [noAuthGuard],
  },
  {
    path: 'dashboard',
    component: TrackedProductsComponent,
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
