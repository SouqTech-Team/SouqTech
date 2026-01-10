import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatPaginatorIntl, PageEvent } from '@angular/material/paginator';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, combineLatest, delay, map, switchMap, take, takeUntil, tap } from 'rxjs';
import { CartService } from 'src/app/services/cart.service';
import { ProductCategoryService } from 'src/app/services/product-category.service';
import { ProductService } from 'src/app/services/product.service';
import { WishlistService } from 'src/app/services/wishlist.service';
import { ToastService } from 'src/app/services/toast.service';
import { AuthService } from 'src/app/services/auth.service';
import { PaginatorOpts } from 'src/app/shared/models/paginator.opts.model';
import { Product } from 'src/app/shared/models/product.model';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss'],
})
export class ProductsComponent implements OnInit, OnDestroy {
  /* Paginator (1) */
  length = 0;
  pageSizeOptions = [6, 12, 24];
  /* Events */
  private destroy$ = new Subject<void>();
  /* States (1) */
  public query$: BehaviorSubject<string> = new BehaviorSubject("");
  public selectedCategory$: BehaviorSubject<number | null> = new BehaviorSubject<number | null>(null);
  public sortBy$: BehaviorSubject<string> = new BehaviorSubject<string>("name,asc");

  private loadingStatusSubject$ = new BehaviorSubject<boolean>(false);
  public loadingStatus$ = this.loadingStatusSubject$;
  private paginatorOpts$: BehaviorSubject<PaginatorOpts> = new BehaviorSubject<PaginatorOpts>({ pageIndex: 0, pageSize: this.pageSizeOptions[0] });
  /* Paginator (2) */
  pageSize = this.paginatorOpts$.value.pageSize;
  pageIndex = this.paginatorOpts$.value.pageIndex;
  /* States (2)*/
  public products$: Observable<Product[]> =
    combineLatest([
      this.query$.pipe(takeUntil(this.destroy$)),
      this.paginatorOpts$.pipe(takeUntil(this.destroy$)),
      this.selectedCategory$.pipe(takeUntil(this.destroy$)),
      this.sortBy$.pipe(takeUntil(this.destroy$))
    ])
      .pipe(
        tap(() => this.loadingStatusSubject$.next(true)),
        switchMap(([query, { pageIndex, pageSize }, categoryId, sortBy]) => {
          // Note: The Backend might need specific params for category and sort
          // If the current backend doesn't support them yet, we'll need to adapt
          return this.productService.products$(query, pageIndex, pageSize);
        }),
        tap(() => this.loadingStatusSubject$.next(false)),
        tap(res => this.length = res.total),
        map(res => res.list),
        // Local filtering if backend doesn't handle all filters yet
        map(list => {
          let filtered = [...list];
          if (this.selectedCategory$.value) {
            filtered = filtered.filter(p => p.categoryId === this.selectedCategory$.value);
          }
          const [field, order] = this.sortBy$.value.split(',');
          filtered.sort((a: any, b: any) => {
            const valA = a[field];
            const valB = b[field];
            return order === 'asc' ? (valA > valB ? 1 : -1) : (valA < valB ? 1 : -1);
          });
          return filtered;
        }),
        takeUntil(this.destroy$)
      );

  constructor(
    public productCategoryService: ProductCategoryService,
    public productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private toastService: ToastService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    // Listen to URL query params (from header search)
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        if (params['search']) {
          this.handleQueryEvent(params['search']);
        }
      });

    // [Set] pageIndex, pageSize [Do] scroll window to top [When] paginatorOpts$
    this.paginatorOpts$
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe(paginatorOpts => {
        this.pageIndex = paginatorOpts.pageIndex;
        this.pageSize = paginatorOpts.pageSize;
        window.scroll({
          top: 0,
          behavior: 'smooth'
        });
      })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  addProductToCart(id: number): void {
    this.cartService.addProduct(id);
  }

  selectProduct(id: number) {
    this.router.navigate(['/products', id]).then();
  }

  addToWishlist(e: Event, id: number) {
    e.stopPropagation();
    if (!this.authService.currentUserSubject$.value) {
      this.toastService.show('Veuillez vous connecter pour ajouter à vos favoris');
      this.router.navigate(['/authenticate']);
      return;
    }
    this.wishlistService.addToWishlist(id).subscribe({
      next: () => {
        this.toastService.show('Produit ajouté aux favoris ❤️');
      },
      error: () => {
        this.toastService.show('Déjà dans vos favoris ou erreur serveur');
      }
    });
  }

  handlePageEvent(e: PageEvent): void {
    // Emit new paginator options
    this.paginatorOpts$.next({ pageIndex: e.pageIndex, pageSize: e.pageSize });
  }

  handleQueryEvent(query: string): void {
    // Emit new paginator options (set pageIndex = 0)
    this.paginatorOpts$.next({ ...this.paginatorOpts$.value, pageIndex: 0 })
    // Emit new query
    this.query$.next(query);
  }

}
