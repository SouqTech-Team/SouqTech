import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';

export interface Review {
    id: number;
    rating: number;
    comment: string;
    userName: string;
    createdAt: string;
    helpfulCount: number;
}

export interface PaginatedReviews {
    content: Review[];
    totalElements: number;
    totalPages: number;
}

@Injectable({
    providedIn: 'root'
})
export class ReviewService {
    private apiUrl = `${environment.apiUrl}/reviews`;

    constructor(private http: HttpClient) { }

    getProductReviews(productId: number, page: number = 0, size: number = 5): Observable<PaginatedReviews> {
        return this.http.get<PaginatedReviews>(`${this.apiUrl}/product/${productId}?page=${page}&size=${size}`);
    }

    getAverageRating(productId: number): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/product/${productId}/rating`);
    }

    addReview(productId: number, rating: number, comment: string): Observable<Review> {
        return this.http.post<Review>(`${this.apiUrl}/product/${productId}`, { rating, comment });
    }

    markAsHelpful(reviewId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${reviewId}/helpful`, {});
    }
}
