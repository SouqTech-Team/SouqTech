import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CartItem } from '../../shared/models/cart.model';

@Component({
  selector: 'app-cart-items-list',
  templateUrl: './cart-items-list.component.html',
  styleUrls: ['./cart-items-list.component.scss']
})
export class CartItemsListComponent {
  @Input() items!: CartItem[];
  @Output() removeItemEvent = new EventEmitter<number>();
  @Output() selectItemEvent = new EventEmitter<number>();
}
