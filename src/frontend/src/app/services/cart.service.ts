import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Cart, LocalStorageCart, CartItem, LocalStorageCartItem } from '../shared/models/cart.model';
import { ToastService } from './toast.service';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // Cart Sidenav Visibility
  public opened$ = new BehaviorSubject<boolean>(false);
  // Cart
  private cart: Cart = new Cart();
  public cart$: BehaviorSubject<Cart> = new BehaviorSubject<Cart>(this.cart);

  constructor(
    private productService: ProductService,
    private toastService: ToastService
  ) {
    this.loadLocalStorageCart();
  }

  /**
   * Loads and syncs the shopping cart from local storage (if any).
   */
  private loadLocalStorageCart(): void {
    // Check if 'cart' item exists in local storage
    if (!localStorage.getItem('cart')) {
      return;
    }

    let localStorageCart: LocalStorageCart | null;
    try {
      // Parse the 'cart' item from local storage
      localStorageCart = JSON.parse(localStorage.getItem('cart')!);
      // Check if the parsed object is an instance of LocalStorageCart
      // (?)
    } catch (error) {
      // Handle errors during parsing
      console.error('Error parsing local storage cart data', error);
      return;
    }

    // Iterate through the items in localStorageCart and add them to the cart
    localStorageCart!.items.forEach((item) => {
      // Retrieve product information (by id) using productService
      this.productService.product$(item.id).subscribe((product) => {
        // Add the product to the cart with its quantity
        this.cart.items.push(<CartItem>{
          product: product,
          quantity: item.quantity,
        });
        // Synchronize the cart with local storage
        this.syncLocalStorage();
      });
    });
    // Update the observable cart
    this.cart$.next(this.cart);
  }

  public removeItemById(id: number): void {
    // Filter out the item with the specified product ID
    this.cart.items = this.cart.items.filter((x) => x.product.id != id);
    // Show a toast notification for successful removal
    this.toastService.show(`Product removed from cart`); //class: ToastClass.SUCCESS_NEGATIVE,
    // Synchronize the cart with local storage
    this.syncLocalStorage();
    // Update the observable cart
    this.cart$.next(this.cart);
  }

  public increaseItemQuantityByProductId(id: number): void {
    this.addProduct(id, 1);
  }

  public decreaseItemQuantityByProductId(id: number): void {
    this.removeProduct(id, 1);
  }

  public addProduct(id: number, quantity: number = 1): void {
    // Fetch the product information by id from the ProductService
    this.productService.product$(id).subscribe((product) => { // (?) the call to ProductService is avoidable
      // Check if the product is already in the cart
      let index = this.cart.items.findIndex((p) => p.product.id == id);

      if (index != -1) { // Product is already in the cart
        if (quantity + this.cart.items[index].quantity <= product.quantity) { // Product quantity in the cart not exceeds availability
          // Update the quantity in the cart
          this.cart.items[index].quantity += quantity;
          // Show a toast notification for successful adding
          this.toastService.show(`Product '${product.name}' added to cart`);//   class: ToastClass.SUCCESS_POSITIVE,
        } else { // Product quantity in the cart exceeds availability
          // Show a toast notification for unsuccessful adding
          this.toastService.show(`No more items available for product '${product.name}'`); //class: ToastClass.DANGER,
        }
      } else { // Product is not in the cart
        // Add product as a new cart item
        this.cart.items.push(<CartItem>{
          product: product,
          quantity: quantity,
        });
        // Show a toast notification for successful adding
        this.toastService.show(`Product '${product.name}' added to cart`); //class: ToastClass.SUCCESS_POSITIVE,
      }
      // Synchronize the cart with local storage
      this.syncLocalStorage();
      // Update the observable cart
      this.cart$.next(this.cart);
    });
  }

  public removeProduct(id: number, quantity: number = 1): void {
    // Fetch the product information by id from the ProductService
    this.productService.product$(id).subscribe((product) => { // (?) the call to ProductService is avoidable
      // Check if the product is already in the cart
      let index = this.cart.items.findIndex((p) => p.product.id == id);

      if (index != -1) { // Product is already in the cart
        if (this.cart.items[index].quantity - quantity > 0) { // Product quantity in the cart is greater then 0
          // Update the quantity in the cart
          this.cart.items[index].quantity -= quantity;
          // Show a toast notification for successful removal
          this.toastService.show(`Product '${product.name}' removed from cart`); //class: ToastClass.SUCCESS_NEGATIVE,
        } else if (this.cart.items[index].quantity - quantity == 0) { // Product quantity in the cart is equal to 0
          // Show a toast notification for unsuccessful removal
          this.toastService.show(`The minimum quantity in the cart is 1 for product '${product.name}'`); //class: ToastClass.DANGER,
        } else { // Product quantity in the cart is less then 0
          // Show a toast notification for unsuccessful removal
          this.toastService.show(`The number of items you are trying to remove is greater than the number of items present for product '${product.name}'`); //class: ToastClass.DANGER,
        }
      }
      // Synchronize the cart with local storage
      this.syncLocalStorage();
      // Update the observable cart
      this.cart$.next(this.cart);
    });
  }

  public clearCart(): void {
    this.cart.items = [];
    localStorage.removeItem('cart');
    this.cart$.next(this.cart);
    this.syncLocalStorage();
  }

  private syncLocalStorage(): void {
    // Create a new LocalStorageCart instance
    let localStorageCart: LocalStorageCart = new LocalStorageCart();
    // Iterate through items in the current cart and add them to the localStorageCart
    this.cart.items.forEach((item) => {
      localStorageCart.items.push(<LocalStorageCartItem>{
        id: item.product.id,
        quantity: item.quantity,
      });
    });
    // Serialize and store the localStorageCart in local storage as 'cart' item
    localStorage.setItem('cart', JSON.stringify(localStorageCart));
  }

  openCart(): void {
    this.opened$.next(true);
  }

  closeCart(): void {
    this.opened$.next(false);
  }

}
