import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { isNotAuthenticatedGuard } from './auth/guards/is-not-authenticated.guard';
import { isAuthenticatedGuard } from './auth/guards/is-authenticated.guard';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule)
  },
  {
    path: 'authenticate',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule),
    canActivate: [isNotAuthenticatedGuard]
  },
  {
    path: 'settings',
    loadChildren: () => import('./features/settings/settings.module').then(m => m.SettingsModule),
    canActivate: [isAuthenticatedGuard]
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule),
    canActivate: [isAuthenticatedGuard]
  },
  {
    path: 'wishlist',
    loadChildren: () => import('./features/wishlist/wishlist.module').then(m => m.WishlistModule),
    canActivate: [isAuthenticatedGuard]
  },
  {
    path: 'products',
    loadChildren: () => import('./features/products/products.module').then(m => m.ProductsModule)
  },
  {
    path: 'about', loadChildren: () => import('./features/about/about.module').then(m => m.AboutModule)
  },
  {
    path: 'cart',
    loadChildren: () => import('./features/cart/cart.module').then(m => m.CartModule)
  },
  {
    path: 'error',
    loadChildren: () => import('./features/error/error.module').then(m => m.ErrorModule)
  },
  {
    path: '**',
    redirectTo: '/error/404'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: "top" })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
