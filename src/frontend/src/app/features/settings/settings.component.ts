import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { BehaviorSubject, Subject, catchError, exhaustMap, of, takeUntil, tap } from 'rxjs';
import { User } from 'src/app/shared/models/auth.model';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';
import { BasicErrorStateMatcher } from 'src/app/shared/utils/error-state-matchers';
import { MatDialog } from '@angular/material/dialog';
import { LogoutConfirmationDialogComponent } from 'src/app/shared/components/logout-confirmation-dialog/logout-confirmation-dialog.component';
import { Status } from 'src/app/shared/models/status.model';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit, OnDestroy {
  /* States */
  currentUser$ = this.authService.currentUser$;
  status$ = new BehaviorSubject<Status>('initial');
  /* Events */
  update$ = new Subject<User>();
  private destroy$ = new Subject<void>();
  /* Form */
  form = this.fb.group({
    email: [{value: '', disabled: true}],
    name: ['', [Validators.required]],
    surname: ['', [Validators.required]],
  });
  basicErrorStateMatcher = new BasicErrorStateMatcher();
  
  constructor(
    private fb: FormBuilder,
    public authService: AuthService,
    private userService: UserService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // [When] update$
    this.update$
      .pipe(
        // [Set] status$ to 'pending'
        tap(() => this.status$.next('pending')),
        // [Make] call to service (return: true for succes, false for failure)
        exhaustMap(
          user => this.userService.updateUser$(user)
            .pipe(
              catchError(() => of(false)),
              tap(x => {if(x) {this.authService.currentUserSubject$.next(user)}})
            )
        ),
        // [Set] status$ to 'success' or 'error'
        tap((user) => this.status$.next(user ? 'success' : 'error')),
        takeUntil(this.destroy$))
    .subscribe();

    // [Set] form values [When] currentUser$
    this.currentUser$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      if (user) {
        this.form.patchValue({
          name: user.name,
          surname: user.surname,
          email: user.email,
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFormSubmit(): void {
    if (this.form.valid) {
      const user = {
        name: this.name.value,
        surname: this.surname.value,
        email: this.email.value
      }
      this.update$.next(user);
    }
  }

  logout(): void {
    const dialogRef = this.dialog.open(LogoutConfirmationDialogComponent);
    dialogRef.afterClosed()
      .pipe(
        takeUntil(this.destroy$)) // (?) afterClose 'finit' or 'infinit' obs?
      .subscribe(result => {
        if(result) {
          this.authService.logout();
        }
    });
  }

  /* Getters to access form controls */
  get name(): FormControl {
    return this.form.get('name')! as FormControl;
  }
  get surname(): FormControl {
    return this.form.get('surname')! as FormControl;
  }
  get email(): FormControl {
    return this.form.get('email')! as FormControl;
  }

}
