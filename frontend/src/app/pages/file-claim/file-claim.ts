import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-file-claim',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './file-claim.html',
  styleUrls: ['./file-claim.scss']
})
export class FileClaimComponent implements OnInit {
  userPolicies: any[] = [];
  loading = true;
  submitting = false;
  uploadMessage = '';

  form = {
    userPolicyId: null as number | null,
    reason: '',
    description: '',
    claimAmount: null as number | null,
    documentUrl: ''
  };

  selectedFile: File | null = null;

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    this.api.getUserPolicies(userId).subscribe({
      next: (res) => {
        this.userPolicies = res.filter(p => p.status === 'ACTIVE');
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files?.[0] || null;
  }

  uploadDocument(): void {
    if (!this.selectedFile) return;

    this.uploadMessage = 'Uploading document...';

    this.api.uploadClaimDocument(this.selectedFile).subscribe({
      next: (res) => {
        this.form.documentUrl = res.documentUrl;
        this.uploadMessage = 'Document uploaded successfully';
      },
      error: () => {
        this.uploadMessage = 'Document upload failed';
      }
    });
  }

  submitClaim(): void {
    if (!this.form.userPolicyId || !this.form.reason || !this.form.claimAmount) {
      alert('Please fill all required fields');
      return;
    }

    this.submitting = true;
    const userId = this.auth.getUserId()!;

    this.api.submitClaim(userId, this.form).subscribe({
      next: () => {
        alert('Claim submitted successfully');
        this.router.navigate(['/my-claims']);
      },
      error: (err) => {
        this.submitting = false;
        alert(err?.error?.message || 'Failed to submit claim');
      }
    });
  }
}
