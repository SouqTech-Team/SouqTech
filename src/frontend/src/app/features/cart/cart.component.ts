import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { CartService } from 'src/app/services/cart.service';
import { Cart, CartItem } from 'src/app/shared/models/cart.model';
import { OrderService } from 'src/app/services/order.service';
import { ToastService } from 'src/app/services/toast.service';

import { ProductCategoryService } from 'src/app/services/product-category.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit, OnDestroy {
  cart: Cart = new Cart();
  /* Table */
  displayedColumns: string[] = ['action', 'image', 'name', 'price', 'quantity', 'subtotal'];
  dataSource!: MatTableDataSource<CartItem>;
  /* Events */
  private destroy$ = new Subject<void>();

  constructor(
    public cartService: CartService,
    public router: Router,
    private orderService: OrderService,
    private toastService: ToastService,
    public productCategoryService: ProductCategoryService
  ) { }

  ngOnInit(): void {
    this.cartService.cart$
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe((cart) => {
        this.cart = cart;
        this.dataSource = new MatTableDataSource(this.cart.items);
      });

  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  removeItem(cartItem: CartItem): void {
    this.cartService.removeItemById(cartItem.product.id);
  }

  selectProduct(cartItem: CartItem): void {
    this.router.navigate(['/products', cartItem.product.id]).then();
  }

  increaseItemQuantity(cartItem: CartItem): void {
    this.cartService.increaseItemQuantityByProductId(cartItem.product.id);
  }

  decreaseItemQuantity(cartItem: CartItem): void {
    this.cartService.decreaseItemQuantityByProductId(cartItem.product.id);
  }

  checkout(): void {
    const productIds: number[] = [];
    this.cart.items.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        productIds.push(item.product.id);
      }
    });

    this.orderService.placeOrder(productIds).subscribe({
      next: (order) => {
        this.toastService.show('Order placed successfully! ID: ' + order.id, 'success');
        this.cartService.clearCart();
        // Redirect to Profile/History or Wishlist
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        this.toastService.show('Failed to place order: ' + (err.error?.message || err.message), 'error');
      }
    });
  }

}
