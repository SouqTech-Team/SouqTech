import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { WishlistComponent } from './wishlist.component';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';

const routes: Routes = [
    { path: '', component: WishlistComponent }
];

@NgModule({
    declarations: [WishlistComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        MatListModule,
        MatIconModule,
        MatButtonModule,
        MatSlideToggleModule,
        MatProgressSpinnerModule,
        MatCardModule,
        MatTooltipModule
    ]
})
export class WishlistModule { }
