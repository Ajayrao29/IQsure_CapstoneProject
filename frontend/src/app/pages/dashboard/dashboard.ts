import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-dashboard', standalone: true, imports: [CommonModule, RouterLink], templateUrl: './dashboard.html', styleUrls: ['./dashboard.scss'] })
export class DashboardComponent implements OnInit {
  user: any = null; myBadges: any[] = []; myAttempts: any[] = []; loading = true; totalSavings = 0;
  constructor(public auth: AuthService, private api: ApiService) {}
  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    this.api.getProfile(userId).subscribe(u => { this.user = u; this.loading = false; });
    this.api.getBadgesByUser(userId).subscribe(b => this.myBadges = b);
    this.api.getAttemptsByUser(userId).subscribe(a => this.myAttempts = a.slice(0, 5));
    this.api.getUserPolicies(userId).subscribe(p => {
      this.totalSavings = p.reduce((sum, pol) => sum + pol.savedAmount, 0);
    });
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
