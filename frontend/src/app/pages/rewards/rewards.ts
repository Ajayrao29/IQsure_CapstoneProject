/*
 * FILE: rewards.ts | LOCATION: pages/rewards/
 * PURPOSE: Rewards page (URL: /rewards). Shows all available rewards. Users can redeem rewards (once each).
 *          Checks for expiry and duplicate redemption.
 * TEMPLATE: rewards.html | STYLES: rewards.scss
 * CALLS: api.service.ts → getAllRewards(), getRewardsByUser(), redeemReward() → RewardController
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-rewards', standalone: true, imports: [CommonModule], templateUrl: './rewards.html', styleUrls: ['./rewards.scss'] })
export class RewardsComponent implements OnInit {
  rewards: any[] = [];
  redeemedIds: Set<number> = new Set();
  loading = true; message = ''; messageType: 'success' | 'error' = 'success';
  constructor(private api: ApiService, private auth: AuthService) {}
  ngOnInit(): void {
    this.api.getAllRewards().subscribe(r => { this.rewards = r; this.loading = false; });
    this.api.getRewardsByUser(this.auth.getUserId()!).subscribe(mine => mine.forEach((r: any) => this.redeemedIds.add(r.rewardId)));
  }
  isRedeemed(id: number): boolean { return this.redeemedIds.has(id); }
  isExpired(date: string): boolean { return new Date(date) < new Date(); }
  redeem(rewardId: number): void {
    this.api.redeemReward(rewardId, this.auth.getUserId()!).subscribe({
      next: () => { this.redeemedIds.add(rewardId); this.message = 'Reward redeemed! 🎉'; this.messageType = 'success'; setTimeout(() => this.message = '', 3000); },
      error: (err: any) => { this.message = err.error?.message || 'Could not redeem.'; this.messageType = 'error'; setTimeout(() => this.message = '', 3000); }
    });
  }
}
