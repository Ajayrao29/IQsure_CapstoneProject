import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/models';

// Simple service to store and read the logged-in user info from localStorage
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly KEY = 'iqsure_user';

  // Save user after login/register
  saveUser(user: AuthResponse): void {
    localStorage.setItem(this.KEY, JSON.stringify(user));
  }

  // Get the currently logged-in user (or null)
  getUser(): AuthResponse | null {
    const data = localStorage.getItem(this.KEY);
    return data ? JSON.parse(data) : null;
  }

  // Is someone logged in?
  isLoggedIn(): boolean {
    return this.getUser() !== null;
  }

  // Is the logged-in user an admin?
  isAdmin(): boolean {
    return this.getUser()?.role === 'ROLE_ADMIN';
  }

  // Get user ID of logged-in user
  getUserId(): number | null {
    return this.getUser()?.userId ?? null;
  }

  // Get JWT token
  getToken(): string | null {
    return this.getUser()?.token ?? null;
  }

  // Log out
  logout(): void {
    localStorage.removeItem(this.KEY);
  }
}

