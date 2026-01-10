import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './products.component';
import { DetailComponent } from './detail/detail.component';

const routes: Routes = [
  { path: '', component: ProductsComponent, pathMatch: 'full' },
  { path: ':id', component: DetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProductsRoutingModule { }
