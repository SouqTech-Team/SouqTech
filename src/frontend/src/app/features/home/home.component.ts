import { Component, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ProductService } from 'src/app/services/product.service';
import { Product } from 'src/app/shared/models/product.model';
import { Observable, map } from 'rxjs';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, MatIconModule, MatButtonModule, CommonModule, MatCardModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private productService = inject(ProductService);
  featuredProducts$!: Observable<Product[]>;

  ngOnInit(): void {
    // Fetch first 4 products for the featured section
    this.featuredProducts$ = this.productService.products$('', 0, 4).pipe(
      map(response => response.list)
    );
  }
}
