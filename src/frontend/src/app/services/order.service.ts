import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface Order {
    id: number;
    products: any[];
    totalAmount: number;
    status: string;
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {

    private apiUrl = environment.apiUrl + '/order';

    constructor(private http: HttpClient) { }

    // Créer une commande avec une liste d'IDs produit
    placeOrder(productIds: number[]): Observable<Order> {
        return this.http.post<Order>(this.apiUrl, productIds);
    }

    // Récupérer mes commandes
    getMyOrders(): Observable<Order[]> {
        return this.http.get<Order[]>(this.apiUrl);
    }
}
