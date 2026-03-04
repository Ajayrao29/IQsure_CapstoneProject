import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-admin-users', standalone: true, imports: [CommonModule], templateUrl: './users.html', styleUrls: ['./users.scss'] })
export class AdminUsersComponent implements OnInit {
  users: any[] = []; loading = true;
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getAllUsers().subscribe(u => { this.users = u; this.loading = false; }); }
  deleteUser(userId: number): void {
    if (!confirm('Delete this user?')) return;
    this.api.deleteUser(userId).subscribe(() => this.users = this.users.filter(u => u.userId !== userId));
  }
}
