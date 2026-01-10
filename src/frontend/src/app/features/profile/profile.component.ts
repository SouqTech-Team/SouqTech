import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Order, OrderService } from 'src/app/services/order.service';
import { User } from 'src/app/shared/models/auth.model'; // Correction chemin import
import { AuthService } from 'src/app/services/auth.service'; // Utilisation AuthService

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  orders$!: Observable<Order[]>; // Ajout '!'
  currentUser$!: Observable<User | null>; // Ajout '!' + nullable

  constructor(
    private orderService: OrderService,
    private authService: AuthService // Remplacement UserService
  ) { }

  ngOnInit(): void {
    this.currentUser$ = this.authService.currentUser$; // Utilisation flux existant
    // Ou recharger : this.authService.user$;
    this.orders$ = this.orderService.getMyOrders();
  }

}
