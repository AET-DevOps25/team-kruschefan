import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { UserProfile } from '../../interfaces/UserProfile';
import { UserService } from '../../services/user.service';
import { KeycloakService } from '../../services/keycloak.service';
import { catchError, finalize, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'forms-ai-user-profile',
  imports: [
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss',
})
export class UserProfileComponent implements OnInit {
  protected userProfile: UserProfile | null = null;
  protected isUserServiceLoading = signal(false);
  private userName: string | null = null;
  private userService = inject(UserService);
  private keyCloakService = inject(KeycloakService);
  private destroyRef = inject(DestroyRef);
  private matSnackbar = inject(MatSnackBar);

  ngOnInit() {
    if (this.keyCloakService.IsLoggedIn) {
      this.isUserServiceLoading.set(true);
      this.loadUserName();
    } else {
      console.warn('User is not logged in, cannot load profile.');
    }
  }

  protected updateProfile(): void {
    if (this.userProfile && this.userName) {
      this.userService
        .updateUserProfile(this.userName, this.userProfile)
        .pipe(
          catchError((error) => {
            console.error('Failed to update user profile:', error);
            return of(null);
          }),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((updatedProfile) => {
          if (updatedProfile) {
            this.matSnackbar.open('Profile updated successfully!', 'Close', {
              duration: 3000,
            });
          } else {
            this.matSnackbar.open(
              'Failed to update profile. Please try again.',
              'Close',
              {
                duration: 3000,
              },
            );
          }
        });
    } else {
      console.warn(
        'User profile or username is not set, cannot update profile.',
      );
    }
  }

  private loadUserName(): void {
    this.keyCloakService.UserProfile?.then((profile) => {
      this.userName = profile.username ?? '';
      this.loadUserProfile();
    }).catch((error) => {
      console.error('Failed to load user profile:', error);
      this.isUserServiceLoading.set(false);
    });
  }

  private loadUserProfile(): void {
    this.userService
      .getUserProfile(this.userName ?? '')
      .pipe(
        catchError((error) => {
          console.error('Failed to load user profile:', error);
          return of(null);
        }),
        finalize(() => {
          this.isUserServiceLoading.set(false);
        }),
        tap((profile) => {
          this.userProfile = profile;
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }
}
