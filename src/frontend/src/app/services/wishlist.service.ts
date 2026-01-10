import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Wishlist } from '../shared/models/wishlist.model';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class WishlistService {

    private apiUrl = `${environment.apiUrl}/wishlist`; // http://localhost:8080/api/wishlist

    constructor(private http: HttpClient) { }

    getMyWishlist(): Observable<Wishlist> {
        return this.http.get<Wishlist>(this.apiUrl);
    }

    addToWishlist(productId: number): Observable<Wishlist> {
        return this.http.post<Wishlist>(`${this.apiUrl}/add/${productId}`, {});
    }

    removeFromWishlist(productId: number): Observable<Wishlist> {
        return this.http.delete<Wishlist>(`${this.apiUrl}/remove/${productId}`);
    }

    toggleSharing(): Observable<Wishlist> {
        return this.http.post<Wishlist>(`${this.apiUrl}/share/toggle`, {});
    }

    getSharedWishlist(token: string): Observable<Wishlist> {
        return this.http.get<Wishlist>(`${this.apiUrl}/shared/${token}`);
    }
}
