import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-claims',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './my-claims.html',
  styleUrls: ['./my-claims.scss']
})
export class MyClaimsComponent implements OnInit {
  claims: any[] = [];
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    this.api.getClaimsByUser(this.auth.getUserId()!).subscribe({
      next: (res) => {
        this.claims = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUBMITTED': return 'submitted';
      case 'UNDER_REVIEW': return 'review';
      case 'APPROVED': return 'approved';
      case 'REJECTED': return 'rejected';
      case 'PAID': return 'paid';
      default: return '';
    }
  }
}
