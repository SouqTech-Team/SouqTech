import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreModule } from './core/core.module';
import { RouterModule } from '@angular/router';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { JwtService } from './services/jwt.service';
import { AuthService } from './services/auth.service';
import { EMPTY, Observable } from 'rxjs';
import { TokenInterceptor } from './auth/interceptors/token.interceptor';
import { MaterialModule } from './material.module';
import { ErrorModule } from './features/error/error.module';


export function initializeAuth(jwtService: JwtService, authService: AuthService): () => Observable<any> {
  return () => (jwtService.getToken()  ? authService.user$ : EMPTY); 
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CoreModule,
    RouterModule,
    HttpClientModule,
    MaterialModule,
    ErrorModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [JwtService, AuthService],
      multi: true,
    },
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
