import { Routes } from '@angular/router';
import { TrackedProductsComponent } from './pages/tracked-products/tracked-products.component';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard } from './core/guards/auth.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    title: 'Scoutly - Nunca mais perca uma queda de preço',
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Entrar | Scoutly',
    canActivate: [noAuthGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    title: 'Criar Conta | Scoutly',
    canActivate: [noAuthGuard],
  },
  {
    path: 'dashboard',
    component: TrackedProductsComponent,
    title: 'Meu Painel | Scoutly',
    canActivate: [authGuard],
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    title: 'Recuperar Senha | Scoutly',
    canActivate: [noAuthGuard],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
