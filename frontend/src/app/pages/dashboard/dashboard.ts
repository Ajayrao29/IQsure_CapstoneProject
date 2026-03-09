/*
 * FILE: dashboard.ts | LOCATION: pages/dashboard/
 * PURPOSE: User dashboard page (URL: /dashboard). Shows overview of user's progress:
 *          profile info, total points, badges earned, recent quiz attempts, total savings.
 * TEMPLATE: dashboard.html | STYLES: dashboard.scss
 * CALLS: api.service.ts → getProfile(), getBadgesByUser(), getAttemptsByUser(), getUserPolicies()
 * BACKEND: UserController, BadgeController, AttemptController, UserPolicyController
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { forkJoin, Subject, takeUntil } from 'rxjs';

@Component({ selector: 'app-dashboard', standalone: true, imports: [RouterLink], templateUrl: './dashboard.html', styleUrls: ['./dashboard.scss'] })
export class DashboardComponent implements OnInit, OnDestroy {
  user: any = null; myBadges: any[] = []; myAttempts: any[] = []; loading = true; totalSavings = 0;
  private destroy$ = new Subject<void>();

  constructor(public auth: AuthService, private api: ApiService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    forkJoin({
      profile: this.api.getProfile(userId),
      badges: this.api.getBadgesByUser(userId),
      attempts: this.api.getAttemptsByUser(userId),
      policies: this.api.getUserPolicies(userId)
    }).pipe(takeUntil(this.destroy$))
      .subscribe(({ profile, badges, attempts, policies }) => {
        this.user = profile;
        this.myBadges = badges;
        this.myAttempts = attempts.slice(0, 5);
        this.totalSavings = policies.reduce((sum, pol) => sum + pol.savedAmount, 0);
        this.loading = false;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get recentAttempts(): any[] { return this.myAttempts; }

  shareAchievement(): void {
    const text = `I've earned ${this.user?.userPoints} points on IQsure! Join me in learning about insurance and saving money! 💪🏼💰`;
    if (navigator.share) {
      navigator.share({ title: 'IQsure Achievement', text });
    } else {
      navigator.clipboard.writeText(text);
      alert('Achievement copied to clipboard!');
    }
  }
}
