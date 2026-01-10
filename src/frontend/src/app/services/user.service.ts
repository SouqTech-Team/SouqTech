import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, delay } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { User } from '../shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private API_URL = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) {}

  // PATCH /user {user}
  public updateUser$(userUpdated: User): Observable<boolean> {
    return this.http
      .patch<User>(`${this.API_URL}`, userUpdated)
      .pipe(
        delay(1000), // (D)
        map((res) => !!res)
      );
  }

}
