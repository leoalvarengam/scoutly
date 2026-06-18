import { Routes } from '@angular/router';
import { TrackedProductsComponent } from './pages/tracked-products/tracked-products.component';

export const routes: Routes = [
  {
    path: '',
    component: TrackedProductsComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
