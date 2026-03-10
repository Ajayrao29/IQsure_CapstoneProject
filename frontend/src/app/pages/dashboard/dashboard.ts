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
 import { CommonModule } from '@angular/common';
 import { ApiService } from '../../services/api.service';
 import { AuthService } from '../../services/auth.service';
 import { forkJoin, Subject, takeUntil } from 'rxjs';

 @Component({
   selector: 'app-dashboard',
   standalone: true,
   imports: [CommonModule, RouterLink],
   templateUrl: './dashboard.html',
   styleUrls: ['./dashboard.scss']
 })
 export class DashboardComponent implements OnInit, OnDestroy {
   user: any = null;

   // user dashboard data
   myBadges: any[] = [];
   myAttempts: any[] = [];
   totalSavings = 0;

   // admin dashboard data
   allUsers: any[] = [];
   allQuizzes: any[] = [];
   allPolicies: any[] = [];
   allRewards: any[] = [];
   allDiscountRules: any[] = [];

   loading = true;
   private destroy$ = new Subject<void>();

   constructor(public auth: AuthService, private api: ApiService) {}

   ngOnInit(): void {
     const userId = this.auth.getUserId();

     if (!userId) {
       this.loading = false;
       return;
     }

     if (this.isAdmin) {
       this.loadAdminDashboard(userId);
     } else {
       this.loadUserDashboard(userId);
     }
   }

   loadUserDashboard(userId: number): void {
     forkJoin({
       profile: this.api.getProfile(userId),
       badges: this.api.getBadgesByUser(userId),
       attempts: this.api.getAttemptsByUser(userId),
       policies: this.api.getUserPolicies(userId)
     })
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: ({ profile, badges, attempts, policies }) => {
           this.user = profile;
           this.myBadges = badges;
           this.myAttempts = attempts.slice(0, 5);
           this.totalSavings = policies.reduce((sum: number, pol: any) => sum + (pol.savedAmount || 0), 0);
           this.loading = false;
         },
         error: (err) => {
           console.error('Failed to load user dashboard', err);
           this.loading = false;
         }
       });
   }

   loadAdminDashboard(userId: number): void {
     forkJoin({
       profile: this.api.getProfile(userId),
       users: this.api.getAllUsers(),
       quizzes: this.api.getAllQuizzes(),
       policies: this.api.getAllPolicies(),
       rewards: this.api.getAllRewards(),
       rules: this.api.getAllDiscountRules()
     })
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: ({ profile, users, quizzes, policies, rewards, rules }) => {
           this.user = profile;
           this.allUsers = users || [];
           this.allQuizzes = quizzes || [];
           this.allPolicies = policies || [];
           this.allRewards = rewards || [];
           this.allDiscountRules = rules || [];
           this.loading = false;
         },
         error: (err) => {
           console.error('Failed to load admin dashboard', err);
           this.loading = false;
         }
       });
   }

   get isAdmin(): boolean {
     return this.auth.isAdmin();
   }

   get recentAttempts(): any[] {
     return this.myAttempts;
   }

   get totalUsers(): number {
     return this.allUsers.length;
   }

   get totalAdmins(): number {
     return this.allUsers.filter(u => u.role === 'ROLE_ADMIN' || u.role === 'ADMIN').length;
   }

   get totalNormalUsers(): number {
     return this.allUsers.filter(u => u.role !== 'ROLE_ADMIN' && u.role !== 'ADMIN').length;
   }

   get totalQuizzes(): number {
     return this.allQuizzes.length;
   }

   get totalPolicies(): number {
     return this.allPolicies.length;
   }

   get activePolicies(): number {
     return this.allPolicies.filter(p => p.isActive === true || p.active === true).length;
   }

   get inactivePolicies(): number {
     return this.allPolicies.filter(p => p.isActive === false || p.active === false).length;
   }

   get totalRewardsIssued(): number {
     return this.allRewards.length;
   }

   get totalDiscountRules(): number {
     return this.allDiscountRules.length;
   }

   shareAchievement(): void {
     const text = `I've earned ${this.user?.userPoints} points on IQsure! Join me in learning about insurance and saving money! 💪🏼💰`;
     if (navigator.share) {
       navigator.share({ title: 'IQsure Achievement', text });
     } else {
       navigator.clipboard.writeText(text);
       alert('Achievement copied to clipboard!');
     }
   }

   ngOnDestroy(): void {
     this.destroy$.next();
     this.destroy$.complete();
   }
 }
