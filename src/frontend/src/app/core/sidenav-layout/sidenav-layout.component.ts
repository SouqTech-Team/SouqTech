import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BehaviorSubject, Subject, takeUntil } from 'rxjs';
import { CartService } from 'src/app/services/cart.service';

@Component({
  selector: 'app-sidenav-layout',
  templateUrl: './sidenav-layout.component.html',
  styleUrls: ['./sidenav-layout.component.scss']
})
export class SidenavLayoutComponent implements OnInit, OnDestroy {
  @ViewChild('sidenav', { static: true }) sidenav!: MatSidenav;
  /* Events */
  private destroy$ = new Subject<void>();
  /* States */
  isOpen!: boolean;

  constructor(
    private cartService: CartService
  ){}

  ngOnInit(): void {
    this.cartService.opened$
    .pipe(
      takeUntil(this.destroy$)
    )
    .subscribe(x => this.sidenav.opened = x)
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
