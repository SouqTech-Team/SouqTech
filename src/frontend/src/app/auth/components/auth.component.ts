import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subject, Observable, merge, map, exhaustMap, catchError, of, takeUntil, skip, withLatestFrom } from 'rxjs';
import { ErrorMessage } from '../constants';
import { Mode, SignInRequest, SignUpRequest, User } from '../../shared/models/auth.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { Status } from 'src/app/shared/models/status.model';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements OnInit, OnDestroy {
  /* States */
  mode$ = new BehaviorSubject<Mode>('signin');
  status$ = new BehaviorSubject<Status>('initial');
  /* Events */
  signIn$ = new Subject<SignInRequest>();
  signUp$ = new Subject<SignUpRequest>();
  private destroy$ = new Subject<void>();
  /* Observables */
  sign$: Observable<boolean> = merge(
    this.signIn$.pipe(map((x): ['signin', SignInRequest] => ['signin', x])),
    this.signUp$.pipe(map((x): ['signup', SignUpRequest] => ['signup', x]))
  ).pipe(
    exhaustMap(([type, credentials]) => {
      const call$ =
        type === 'signin'
          ? this.authService.signIn$(credentials)
          : this.authService.signUp$(credentials); // TODO: change to SingUp
      return call$.pipe(
        catchError(() => of(false)), // (?) Move to AuthService
        takeUntil(this.mode$.pipe(skip(1))) // (N) stops the internal chain (http call) when the mode changes (optimization)
      );
    })
  );

  constructor(private authService: AuthService, private readonly router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    // [Set] 'success' or 'error' status [When] sign$ (signIn or signUp attempt result)
    this.sign$
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe((result) => {
        this.status$.next(result ? 'success' : 'error');
      });

    // [Set] 'pending' status [When] signIn$ | signUp$ (signIn or signUp attempt start)
    merge(this.signIn$, this.signUp$)
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.status$.next('pending');
      });

    // [Set] 'initial' status [When] $mode (tab change)
    this.mode$
    .pipe(
      skip(1),
      takeUntil(this.destroy$))
    .subscribe((mode) => {
      this.status$.next('initial');
    });

    // [Handle] signin/signup status change behaviour [When] status$ 
    this.status$
      .pipe(
        withLatestFrom(this.mode$),
        takeUntil(this.destroy$))
      .subscribe(([status, mode]) => {
        if (status === 'success') {
          if (mode === 'signin') {
            // Navigate to homepage
            this.router.navigate(['']);
          } else { // mode === 'signup'
            // Set 'signin' mode
            this.mode$.next('signin');
          }
        } else if(status === 'error') {
          if (mode === 'signin') {
            this.openErrorSnackBar(ErrorMessage.SIGNIN)
          } else { // mode === 'signup'
            this.openErrorSnackBar(ErrorMessage.SIGNUP)
          }
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  openErrorSnackBar(message: string): void {
    this.snackBar.open(message, undefined, {duration: 2000, verticalPosition: 'bottom'});
  }
  
}
