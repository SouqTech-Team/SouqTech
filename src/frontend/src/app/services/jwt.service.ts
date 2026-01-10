import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class JwtService {
  private localStorageJwtKey = 'jwt_token';
 
  constructor() {}

  getToken(): string | null {
    return localStorage.getItem(this.localStorageJwtKey);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.localStorageJwtKey, token);
  }

  destroyToken(): void {
    localStorage.removeItem(this.localStorageJwtKey);
  }

  // isTokenExpired, isCurrentTokenExpired
}
