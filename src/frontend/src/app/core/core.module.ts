import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { MaterialModule } from '../material.module';
import { RouterModule } from '@angular/router';
import { AuthModule } from '../auth/auth.module';
import { SidenavLayoutComponent } from './sidenav-layout/sidenav-layout.component';
import { CartSidenavContentComponent } from './cart-sidenav-content/cart-sidenav-content.component';
import { SharedModule } from '../shared/shared.module';
import { CartItemComponent } from './cart-item/cart-item.component';
import { CartItemsListComponent } from './cart-items-list/cart-items-list.component';


@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    SidenavLayoutComponent,
    CartSidenavContentComponent,
    CartItemComponent,
    CartItemsListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MaterialModule,
    RouterModule,
    AuthModule,
    SharedModule,
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    SidenavLayoutComponent,
    CartSidenavContentComponent,
    CartItemComponent,
    CartItemsListComponent
  ]
})
export class CoreModule { }
