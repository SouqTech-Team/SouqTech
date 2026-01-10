import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { take, startWith, Observable, catchError, of, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { ProductCategory } from '../shared/models/product.category.model';

@Injectable({
  providedIn: 'root'
})
export class ProductCategoryService {
  API_URL = `${environment.apiUrl}/category`;
  categories!: ProductCategory[];

  constructor(private http: HttpClient) {
    this.productCategories$.pipe(take(1), startWith([])).subscribe(categories => {
      this.categories = categories;
    });
  }

  // GET /category {}
  productCategories$ = <Observable<ProductCategory[]>> this.http.get<ProductCategory[]>(`${this.API_URL}`).pipe(catchError(this.handleError));
  

  // GET /category/[id] {}
  public getProductCategory(id: number): Observable<ProductCategory> {
    // Attempt to find the product category in the local cache
    var productCategory = this.categories.find((x) => x.id == id);
    if (productCategory) {
      // If found in the cache, return it
      return of(productCategory);
    } else {
      // If not found in the cache, fetch it from the API
      return this.http
        .get<ProductCategory>(`${this.API_URL}/${id}`)
        .pipe(
          // Update the local cache with the fetched category data
          tap((newCategory) => {
            this.categories.push(newCategory);
          }),
          take(1),
          catchError(this.handleError)
        );
    }
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    // TODO implement
    console.log(error);
    return throwError(`An error occured - Error code: ${error.status}`);
  }
}
