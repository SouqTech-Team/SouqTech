import { Component, Input, OnDestroy, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';
import { IsLoggedDirective } from 'src/app/auth/directives/is-logged.directive';
import { AuthService } from 'src/app/services/auth.service';
import { CartService } from 'src/app/services/cart.service';
import { LogoutConfirmationDialogComponent } from 'src/app/shared/components/logout-confirmation-dialog/logout-confirmation-dialog.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnDestroy {
  /* BrandName */
  @Input() brandName!: string;
  /* Injection */
  authService = inject(AuthService);
  router = inject(Router);
  /* Observables */
  currentUser$ = this.authService.currentUser$;
  /* Search */
  searchQuery: string = '';
  private searchSubject$ = new Subject<string>();
  /* Events */
  private destroy$ = new Subject<void>();

  constructor(
    public cartService: CartService,
    public dialog: MatDialog
  ) {
    // Setup search with debounce
    this.searchSubject$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(query => {
        if (query.trim()) {
          this.navigateToSearch(query);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearchChange(query: string): void {
    // Emit to debounced search subject
    this.searchSubject$.next(query);
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      this.navigateToSearch(this.searchQuery);
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
  }

  private navigateToSearch(query: string): void {
    this.router.navigate(['/products'], {
      queryParams: { search: query }
    });
  }

  logout(): void {
    const dialogRef = this.dialog.open(LogoutConfirmationDialogComponent);
    dialogRef.afterClosed()
      .pipe(
        takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {
          this.authService.logout();
        }
      });
  }
}
