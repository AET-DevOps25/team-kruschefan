import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { UserProfile } from '../interfaces/UserProfile';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private baseUrl = environment.userApiUrl;

  public getUserProfile(username: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/${username}`);
  }

  public createUserProfile(profile: UserProfile): Observable<UserProfile> {
    return this.http.post<UserProfile>(`${this.baseUrl}`, profile);
  }

  public updateUserProfile(
    username: string,
    profile: UserProfile,
  ): Observable<UserProfile> {
    return this.http.put<UserProfile>(
      `${this.baseUrl}/user/${username}`,
      profile,
    );
  }
}
