import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, distinctUntilChanged, map, tap, concatMap, EMPTY, catchError, of, startWith, delay } from 'rxjs';
import { SignInRequest, SignInResponse, SignUpRequest, SignUpResponse, User } from '../shared/models/auth.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment.development';
import { JwtService } from './jwt.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API_URL = `${environment.apiUrl}/auth`;

  public currentUserSubject$: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> = this.currentUserSubject$.asObservable().pipe(startWith(this.currentUserSubject$.getValue()), distinctUntilChanged());

  public isAuthenticated$: Observable<boolean> = this.currentUserSubject$.asObservable().pipe(map((user) => !!user));

  constructor(private http: HttpClient, private jwtService: JwtService, private router: Router) {}

  public logout(): void {
    this.jwtService.destroyToken();
    this.currentUserSubject$.next(null);
    this.router.navigateByUrl('/home');
  }

  // POST /auth/register {credentials}
  public signUp$(credentials: SignUpRequest): Observable<boolean> {
    return this.http
      .post<SignUpResponse>(`${this.API_URL}/register`, credentials)
      .pipe(
        map((res) => !!res),
        delay(1000), // (D)
      );
  }

  // POST /auth/authenticate {credentials}
  public signIn$(credentials: SignInRequest): Observable<boolean> {
    return this.http
      .post<SignInResponse>(`${this.API_URL}/authenticate`, credentials)
      .pipe(
        delay(1000), // (D)
        tap((res) => this.jwtService.saveToken(res.token)),
        concatMap(() => this.user$),
        map((res) => !!res),
      );
  }

  // GET /auth/me {}
  public user$: Observable<User | null> = this.http
    .get<User>(`${this.API_URL}/me`)
    .pipe(
      tap((user) => (!!user ? this.currentUserSubject$.next(user) : EMPTY)), // (?) EMPTY or this.currentUserSubject$.next(null)
      catchError((err) => of(null))
    );

}
