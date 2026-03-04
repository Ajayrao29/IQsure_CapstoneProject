import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  // ─── Auth ────────────────────────────────────────────────────────────────
  register(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/auth/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/auth/login`, data);
  }

  // ─── Users ───────────────────────────────────────────────────────────────
  getProfile(userId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/users/${userId}`);
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users`);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/users/${userId}`);
  }

  getLeaderboard(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/leaderboard`);
  }

  // ─── Quizzes ─────────────────────────────────────────────────────────────
  getAllQuizzes(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/quizzes`);
  }

  getQuizById(quizId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/quizzes/${quizId}`);
  }

  createQuiz(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/quizzes`, data);
  }

  updateQuiz(quizId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/quizzes/${quizId}`, data);
  }

  deleteQuiz(quizId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/quizzes/${quizId}`);
  }

  // ─── Questions ───────────────────────────────────────────────────────────
  getQuestionsByQuiz(quizId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/questions/quiz/${quizId}`);
  }

  addQuestion(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/questions`, data);
  }

  addAnswer(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/questions/answers`, data);
  }

  deleteQuestion(questionId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/questions/${questionId}`);
  }

  // ─── Attempts ────────────────────────────────────────────────────────────
  // Backend: POST /api/v1/attempts?userId=X  body: { quizId, answers: Map<Long,Integer> }
  submitQuiz(userId: number, data: { quizId: number; answers: { [questionId: number]: number } }): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/attempts?userId=${userId}`, data);
  }

  getAttemptsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/attempts?userId=${userId}`);
  }

  // ─── Badges ──────────────────────────────────────────────────────────────
  getAllBadges(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/badges`);
  }

  getBadgesByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/badges/user/${userId}`);
  }

  createBadge(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/badges`, data);
  }

  deleteBadge(badgeId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/badges/${badgeId}`);
  }

  // ─── Rewards ─────────────────────────────────────────────────────────────
  getAllRewards(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/rewards`);
  }

  // GET /api/v1/rewards/user/{userId} — rewards redeemed by this user
  getRewardsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/rewards/user/${userId}`);
  }

  // POST /api/v1/rewards/{rewardId}/redeem?userId=X
  redeemReward(rewardId: number, userId: number): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/rewards/${rewardId}/redeem?userId=${userId}`, {});
  }

  createReward(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/rewards`, data);
  }

  deleteReward(rewardId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/rewards/${rewardId}`);
  }

  // ─── Policies ────────────────────────────────────────────────────────────
  // GET /api/v1/policies — active only (for users)
  getActivePolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/policies`);
  }

  // GET /api/v1/policies/all — all including inactive (for admin)
  getAllPolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/policies/all`);
  }

  createPolicy(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/policies`, data);
  }

  updatePolicy(policyId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/policies/${policyId}`, data);
  }

  deletePolicy(policyId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/policies/${policyId}`);
  }

  // ─── User Policies & Premium ──────────────────────────────────────────────
  // GET /api/v1/users/{userId}/premium/calculate/{policyId}
  calculatePremium(userId: number, policyId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/users/${userId}/premium/calculate/${policyId}`);
  }

  // POST /api/v1/users/{userId}/policies  body: { policyId }
  purchasePolicy(userId: number, data: { policyId: number }): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/users/${userId}/policies`, data);
  }

  // GET /api/v1/users/{userId}/policies
  getUserPolicies(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/${userId}/policies`);
  }

  // ─── Discount Rules ───────────────────────────────────────────────────────
  // GET /api/v1/discount-rules/all — all rules incl. inactive (admin)
  getAllDiscountRules(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/discount-rules/all`);
  }

  createDiscountRule(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/discount-rules`, data);
  }

  updateDiscountRule(ruleId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/discount-rules/${ruleId}`, data);
  }

  deleteDiscountRule(ruleId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/discount-rules/${ruleId}`);
  }
}

