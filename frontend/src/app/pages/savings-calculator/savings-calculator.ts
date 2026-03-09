/*
 * FILE: savings-calculator.ts | LOCATION: pages/savings-calculator/
 * PURPOSE: Savings Calculator page (URL: /savings). Shows total savings from purchased policies
 *          and potential future savings based on current gamification data.
 * TEMPLATE: savings-calculator.html | STYLES: savings-calculator.scss
 * CALLS: api.service.ts → getUserPolicies(), getProfile(), getActivePolicies(), calculatePremium()
 * BACKEND: UserPolicyController, UserController, PolicyController, PremiumCalculationService
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-savings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './savings-calculator.html',
  styleUrls: ['./savings-calculator.scss']
})
export class SavingsCalculatorComponent implements OnInit {
  totalSavings = 0;
  potentialSavings = 0;
  policies: any[] = [];
  userPoints = 0;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;

    this.api.getUserPolicies(userId).subscribe(policies => {
      this.policies = policies;
      this.totalSavings = policies.reduce((sum, p) => sum + p.savedAmount, 0);
    });

    this.api.getProfile(userId).subscribe(u => {
      this.userPoints = u.userPoints;
      this.calculatePotentialSavings(userId);
    });
  }

  calculatePotentialSavings(userId: number): void {
    this.api.getActivePolicies().subscribe(policies => {
      let potential = 0;
      policies.forEach(p => {
        this.api.calculatePremium(userId, p.policyId).subscribe(calc => {
          potential += calc.discountedAmount;
        });
      });
      setTimeout(() => this.potentialSavings = potential, 1000);
    });
  }
}
