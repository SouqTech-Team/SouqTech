import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { CartService } from 'src/app/services/cart.service';
import { ToastService } from 'src/app/services/toast.service';
import { Cart, CartItem } from 'src/app/shared/models/cart.model';

@Component({
  selector: 'app-cart-sidenav-content',
  templateUrl: './cart-sidenav-content.component.html',
  styleUrls: ['./cart-sidenav-content.component.scss']
})
export class CartSidenavContentComponent implements OnInit, OnDestroy{
  /* Cart */
  cart: Cart = new Cart();
  /* Events */
  private destroy$ = new Subject<void>();

  constructor(
    public cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {      
      this.cartService.cart$
        .pipe(
          takeUntil(this.destroy$)
        )
        .subscribe((cart) => {
          this.cart = cart;
        });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  removeItem(cartItem: CartItem): void {
    this.cartService.removeItemById(cartItem.product.id);
  }

  removeItemById(cartItemId: number): void {
    this.cartService.removeItemById(cartItemId);
  }

  selectProductById(id: number) {
    this.cartService.closeCart();
    this.router.navigate(['/products', id]).then();
  }

  increaseItemQuantity(cartItem: CartItem): void {
    this.cartService.increaseItemQuantityByProductId(cartItem.product.id);
  }

  decreaseItemQuantity(cartItem: CartItem): void {
    this.cartService.decreaseItemQuantityByProductId(cartItem.product.id);
  }

}
