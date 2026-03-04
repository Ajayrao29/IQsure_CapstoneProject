import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-policies', standalone: true, imports: [CommonModule, DecimalPipe], templateUrl: './policies.html', styleUrls: ['./policies.scss'] })
export class PoliciesComponent implements OnInit {
  policies: any[] = []; preview: any = null; selectedPolicyId: number | null = null;
  loadingPreview = false; purchasing = false; message = ''; loading = true;
  constructor(private api: ApiService, private auth: AuthService, private router: Router) {}
  ngOnInit(): void { this.api.getActivePolicies().subscribe(p => { this.policies = p; this.loading = false; }); }
  previewPremium(policyId: number): void {
    this.selectedPolicyId = policyId; this.preview = null; this.loadingPreview = true;
    this.api.calculatePremium(this.auth.getUserId()!, policyId).subscribe({
      next: (b) => { this.preview = b; this.loadingPreview = false; },
      error: () => { this.loadingPreview = false; }
    });
  }
  purchase(): void {
    if (!this.selectedPolicyId) return; this.purchasing = true;
    this.api.purchasePolicy(this.auth.getUserId()!, { policyId: this.selectedPolicyId }).subscribe({
      next: () => { this.message = 'Policy purchased successfully!'; this.purchasing = false; setTimeout(() => this.router.navigate(['/my-policies']), 1500); },
      error: (err: any) => { this.message = err.error?.message || 'Purchase failed.'; this.purchasing = false; }
    });
  }
  closePreview(): void { this.preview = null; this.selectedPolicyId = null; this.message = ''; }
  getPolicyIcon(type: string): string { return type === 'HEALTH' ? '❤️' : type === 'LIFE' ? '🌿' : '🚗'; }
}


