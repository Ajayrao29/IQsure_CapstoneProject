import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-reward-mgmt', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './reward-mgmt.html', styleUrls: ['./reward-mgmt.scss'] })
export class RewardMgmtComponent implements OnInit {
  rewards: any[] = []; loading = true; showForm = false;
  form: any = { rewardType: '', discountValue: null, expiryDate: '' };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getAllRewards().subscribe(r => { this.rewards = r; this.loading = false; }); }
  save(): void { this.api.createReward(this.form).subscribe(r => { this.rewards.push(r); this.form = { rewardType: '', discountValue: null, expiryDate: '' }; this.showForm = false; }); }
  delete(id: number): void { if (!confirm('Delete reward?')) return; this.api.deleteReward(id).subscribe(() => this.rewards = this.rewards.filter(r => r.rewardId !== id)); }
}
