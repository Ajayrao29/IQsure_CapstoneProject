import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-admin-claims',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './admin-claims.html',
  styleUrls: ['./admin-claims.scss']
})
export class AdminClaimsComponent implements OnInit {
  claims: any[] = [];
  loading = true;
  remarks: { [claimId: number]: string } = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.api.getAllClaims().subscribe({
      next: (res) => {
        this.claims = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  moveToReview(claimId: number): void {
    this.api.moveClaimToReview(claimId).subscribe(() => this.loadClaims());
  }

  approve(claimId: number): void {
    this.api.approveClaim(claimId, this.remarks[claimId] || '').subscribe(() => this.loadClaims());
  }

  reject(claimId: number): void {
    this.api.rejectClaim(claimId, this.remarks[claimId] || '').subscribe(() => this.loadClaims());
  }
}
