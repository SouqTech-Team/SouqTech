import { Component, OnInit, inject } from '@angular/core';
import { WishlistService } from 'src/app/services/wishlist.service';
import { Wishlist } from 'src/app/shared/models/wishlist.model';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { CartService } from 'src/app/services/cart.service';
import { ToastService } from 'src/app/services/toast.service';

@Component({
  selector: 'app-wishlist',
  template: `
    <div class="wishlist-page animate-fade-in">
      <div class="hero-section">
        <div class="mesh-gradient"></div>
        <div class="container hero-content">
          <h1 class="animate-up">Ma Liste de Souhaits ❤️</h1>
          <p class="animate-up" style="--delay: 0.1s">Gardez un œil sur les pépites tech que vous adorez.</p>
        </div>
      </div>

      <div class="container content-section">
        <div *ngIf="wishlist$ | async as wishlist; else loadingOrError" class="wishlist-container">
          
          <div class="wishlist-header-actions animate-up" *ngIf="wishlist.products.length > 0" style="--delay: 0.2s">
            <div class="share-toggle">
              <mat-slide-toggle [checked]="wishlist.isPublic" (change)="toggleShare()">
                Partage public
              </mat-slide-toggle>
              <button *ngIf="wishlist.isPublic" mat-stroked-button color="primary" class="share-btn" (click)="copyLink(wishlist.shareToken)">
                <mat-icon>content_copy</mat-icon> Copier le lien
              </button>
            </div>
            <p class="count">{{wishlist.products.length}} article{{wishlist.products.length > 1 ? 's' : ''}} enregistré{{wishlist.products.length > 1 ? 's' : ''}}</p>
          </div>

          <div class="products-grid" *ngIf="wishlist.products.length > 0">
            <mat-card *ngFor="let product of wishlist.products; let i = index" class="wishlist-card hover-lift animate-up" [style.--delay]="(i * 0.1) + 0.3 + 's'">
              <div class="product-img-container" (click)="goToDetail(product.id)">
                <img [src]="product.image" [alt]="product.name">
              </div>
              <mat-card-content (click)="goToDetail(product.id)">
                <h3 class="product-name">{{product.name}}</h3>
                <p class="product-price">{{product.price | currency:'TND':'code'}}</p>
              </mat-card-content>
              <mat-card-actions class="card-actions">
                <button mat-raised-button class="btn-gradient action-btn" (click)="addToCart(product.id)">
                  <mat-icon>shopping_cart</mat-icon> Ajouter
                </button>
                <button mat-icon-button class="remove-btn" (click)="remove(product.id)" matTooltip="Supprimer">
                  <mat-icon>delete_outline</mat-icon>
                </button>
              </mat-card-actions>
            </mat-card>
          </div>

          <div *ngIf="wishlist.products.length === 0" class="empty-state animate-up">
            <div class="empty-icon-wrapper">
              <mat-icon>favorite_border</mat-icon>
            </div>
            <h2>Votre liste est vide</h2>
            <p>Il est temps de remplir votre liste avec les produits les plus innovants !</p>
            <button mat-raised-button class="btn-gradient" routerLink="/products">
              Découvrir les produits
            </button>
          </div>
        </div>

        <ng-template #loadingOrError>
          <div class="loading-state">
            <mat-spinner diameter="40"></mat-spinner>
            <p>Chargement de vos favoris...</p>
          </div>
        </ng-template>
      </div>
    </div>
  `,
  styles: [`
    .wishlist-page { min-height: 100vh; background: #fff; }
    
    .hero-section {
      position: relative;
      padding: 80px 0 120px;
      color: white;
      text-align: center;
      background: #1a1a1a;
      overflow: hidden;
      margin-bottom: -60px;
    }

    .mesh-gradient {
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: radial-gradient(circle at 20% 30%, rgba(103, 58, 183, 0.4) 0%, transparent 40%),
                  radial-gradient(circle at 80% 70%, rgba(3, 169, 244, 0.4) 0%, transparent 40%);
      opacity: 0.8;
    }

    .hero-content { position: relative; z-index: 1; }
    .hero-content h1 { font-size: 3rem; font-weight: 800; margin-bottom: 10px; }
    .hero-content p { font-size: 1.2rem; opacity: 0.8; font-weight: 500; }

    .content-section { position: relative; z-index: 2; padding-bottom: 100px; }

    .wishlist-container {
      background: #fff;
      border-radius: 40px;
      padding: 3rem;
      box-shadow: 0 30px 60px rgba(0,0,0,0.05);
      border: 1px solid #f0f0f0;

      @media (max-width: 600px) { padding: 1.5rem; }
    }

    .wishlist-header-actions {
      display: flex; justify-content: space-between; align-items: center; margin-bottom: 2.5rem;
      @media (max-width: 600px) { flex-direction: column; gap: 1rem; align-items: flex-start; }
    }

    .share-toggle { display: flex; align-items: center; gap: 1rem; }
    .count { font-weight: 800; color: #673ab7; font-size: 1rem; }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 2rem;
    }

    .wishlist-card {
      background: #fff; border-radius: 24px; padding: 1.5rem; border: 1px solid #f0f0f0; overflow: hidden;
      
      .product-img-container {
        height: 200px; background: #f9f9f9; padding: 1rem; border-radius: 16px; margin-bottom: 1rem;
        cursor: pointer; display: flex; align-items: center; justify-content: center;
        img { max-width: 100%; max-height: 100%; object-fit: contain; }
      }

      .product-name { font-size: 1.2rem; font-weight: 800; margin-bottom: 0.5rem; cursor: pointer; &:hover { color: #673ab7; } }
      .product-price { font-size: 1.4rem; font-weight: 900; color: #673ab7; margin: 0; }
    }

    .card-actions {
        display: flex; gap: 10px; align-items: center; justify-content: space-between; padding-top: 1rem !important;
        .action-btn { flex: 1; border-radius: 12px !important; font-weight: 700 !important; mat-icon { margin-right: 8px; } }
        .remove-btn { color: #ff4757; &:hover { background: #fee; } }
    }

    .empty-state {
      padding: 60px 20px;
      .empty-icon { font-size: 80px; width: 80px; height: 80px; color: #cbd5e1; margin-bottom: 20px; }
      h2 { font-weight: 800; margin-bottom: 10px; }
      p { color: #64748b; margin-bottom: 30px; }
    }

    .loading-state {
      padding: 100px 0;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 20px;
      color: #64748b;
    }
  `]
})
export class WishlistComponent implements OnInit {

  wishlist$: Observable<Wishlist> | undefined;

  private wishlistService = inject(WishlistService);
  private router = inject(Router);
  private cartService = inject(CartService);
  private toastService = inject(ToastService);

  constructor() { }

  ngOnInit(): void {
    this.wishlist$ = this.wishlistService.getMyWishlist();
  }

  remove(productId: number) {
    this.wishlistService.removeFromWishlist(productId).subscribe(() => {
      this.wishlist$ = this.wishlistService.getMyWishlist(); // Refresh
    });
  }

  toggleShare() {
    this.wishlistService.toggleSharing().subscribe(() => {
      this.wishlist$ = this.wishlistService.getMyWishlist(); // Refresh
    });
  }

  copyLink(token: string) {
    const url = window.location.origin + '/wishlist/shared/' + token;
    navigator.clipboard.writeText(url);
    this.toastService.show('Lien copié dans le presse-papier !');
  }

  goToDetail(productId: number) {
    this.router.navigate(['/products', productId]);
  }

  addToCart(productId: number) {
    this.cartService.addProduct(productId);
    this.toastService.show('Produit ajouté au panier !');
  }
}
