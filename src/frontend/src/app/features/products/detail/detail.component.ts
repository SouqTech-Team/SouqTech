import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, map, switchMap, takeUntil, tap, of } from 'rxjs';
import { CartService } from 'src/app/services/cart.service';
import { ProductService } from 'src/app/services/product.service';
import { Product } from 'src/app/shared/models/product.model';
import { ReviewService, Review, PaginatedReviews } from 'src/app/services/review.service';
import { AuthService } from 'src/app/services/auth.service';
import { ProductCategoryService } from 'src/app/services/product-category.service';
import { ToastService } from 'src/app/services/toast.service';
import { WishlistService } from 'src/app/services/wishlist.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss'],
})
export class DetailComponent implements OnInit, OnDestroy {
  /* Injection */
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reviewService = inject(ReviewService);
  public authService = inject(AuthService);
  public productCategoryService = inject(ProductCategoryService);
  private toastService = inject(ToastService);
  private wishlistService = inject(WishlistService);

  /* Events */
  private destroy$ = new Subject<void>();

  /* States */
  public id!: number;
  public product$!: Observable<Product>;
  public reviews$!: Observable<PaginatedReviews>;
  public averageRating$!: Observable<number>;

  // New Review Form
  public newRating: number = 5;
  public newComment: string = '';

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id = +idParam;
      this.product$ = this.productService.product$(this.id);
      this.loadReviews();
    }
  }

  loadReviews() {
    this.reviews$ = this.reviewService.getProductReviews(this.id);
    this.averageRating$ = this.reviewService.getAverageRating(this.id);
  }

  submitReview() {
    if (!this.newComment.trim()) return;

    console.log('Submitting review:', { productId: this.id, rating: this.newRating, comment: this.newComment });

    this.reviewService.addReview(this.id, this.newRating, this.newComment)
      .subscribe({
        next: () => {
          this.toastService.show('Review submitted successfully!');
          this.newComment = '';
          this.newRating = 5;
          this.loadReviews();
        },
        error: (err) => {
          console.error('Error submitting review:', err);
          this.toastService.show('Failed to submit review. Please try again.');
        }
      });
  }

  markHelpful(reviewId: number) {
    this.reviewService.markAsHelpful(reviewId).subscribe(() => {
      this.toastService.show('Marked as helpful!');
      this.loadReviews();
    });
  }

  addProductToCart(id: number) {
    this.cartService.addProduct(id);
  }

  addToWishlist(id: number) {
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
