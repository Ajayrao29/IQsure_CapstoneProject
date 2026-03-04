import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-badge-mgmt', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './badge-mgmt.html', styleUrls: ['./badge-mgmt.scss'] })
export class BadgeMgmtComponent implements OnInit {
  badges: any[] = []; loading = true; showForm = false;
  form: any = { name: '', description: '', reqPoints: null };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getAllBadges().subscribe(b => { this.badges = b; this.loading = false; }); }
  save(): void { this.api.createBadge(this.form).subscribe(b => { this.badges.push(b); this.form = { name: '', description: '', reqPoints: null }; this.showForm = false; }); }
  delete(id: number): void { if (!confirm('Delete badge?')) return; this.api.deleteBadge(id).subscribe(() => this.badges = this.badges.filter(b => b.badgeId !== id)); }
}
