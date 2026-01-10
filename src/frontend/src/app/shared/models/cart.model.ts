import { Product } from "./product.model";

/*
    CART
*/


abstract class CartBase<T> {
  public items: T[] = [];
}

export class Cart extends CartBase<CartItem> {
  get total(): number {
    let total = 0;
    this.items.forEach((item) => {
      total += item.quantity * item.product.price;
    });
    return total;
  }

  get quantity(): number {
    let total = 0;
    this.items.forEach((item) => {
      total += item.quantity;
    });
    return total;
  }
}

export class LocalStorageCart extends CartBase<LocalStorageCartItem> {}


/*
    CART ITEM
*/

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface LocalStorageCartItem {
  id: number;
  quantity: number;
}
