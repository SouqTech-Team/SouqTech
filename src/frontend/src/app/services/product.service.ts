import { Injectable } from '@angular/core';
import { Observable, catchError, delay, of, take, tap, throwError } from 'rxjs';
import { Product } from '../shared/models/product.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment.development';
import { PageableResponse } from '../shared/models/pageable.response.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  API_URL = `${environment.apiUrl}/product`;

  constructor(private http: HttpClient) {}

  // GET /product?q=[q]&pageIndex=[pageIndex]&pageSize=[pageSize]  {}
  products$ = (q: string, pageIndex: number, pageSize: number) =>
    <Observable<PageableResponse<Product>>>(
      this.http
        .get<PageableResponse<Product>>(
          `${this.API_URL}?q=${q}&pageNumber=${pageIndex}&pageSize=${pageSize}`
        )
        .pipe(
          catchError(this.handleError),
          delay(1000), // (D)
        )
    );

  // GET /product/[id] {}
  product$ = (id: number) =>
    <Observable<Product>>(
      this.http
        .get<Product>(`${this.API_URL}/${id}`)
        .pipe(
          take(1),
          // delay(1000), // (D)
          catchError(this.handleError)
        )
    );

  /*
  productsByCategoryName$ = (categoryName: string) =>
    <Observable<Product[]>>(
      this.http
        .get<Product[]>(`${this.API_URL}?category=${categoryName}`)
        .pipe(catchError(this.handleError))
  );
 
  productsByQuery$ = (query: string): Observable<Product[]> => {
    if (query.trim() === '') {
      return of([]);
    } else {
      return this.http
        .get<Product[]>(`${this.API_URL}/search?q=${query}`)
        .pipe(catchError(this.handleError));
    }
  };
  */

  private handleError(error: HttpErrorResponse): Observable<never> {
    // TODO: implement
    console.log(error);
    return throwError(`An error occured - Error code: ${error.status}`);
  }
}
