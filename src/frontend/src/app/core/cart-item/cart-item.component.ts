import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CartItem } from '../../shared/models/cart.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart-item',
  templateUrl: './cart-item.component.html',
  styleUrls: ['./cart-item.component.scss']
})
export class CartItemComponent {
  @Input() item!: CartItem;
  @Output() removeItemEvent = new EventEmitter();
  @Output() selectItemEvent = new EventEmitter();
}
