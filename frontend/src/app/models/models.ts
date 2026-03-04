export interface AuthResponse { token: string; tokenType: string; userId: number; name: string; email: string; role: string; }
export interface User { userId: number; name: string; email: string; phone: string; userPoints: number; role: string; }
export interface LeaderboardEntry { rank: number; userId: number; name: string; userPoints: number; quizzesAttempted: number; }
export interface Quiz { quizId: number; title: string; category: string; difficulty: string; totalQuestions: number; }
export interface Question { questionId: number; quizId: number; text: string; options: string[]; }
export interface AttemptResponse { attemptId: number; userId: number; quizId: number; quizTitle: string; score: number; totalQuestions: number; percentage: number; pointsEarned: number; attemptDate: string; newBadgesUnlocked: Badge[]; }
export interface Badge { badgeId: number; name: string; description: string; reqPoints: number; }
export interface Reward { rewardId: number; rewardType: string; discountValue: number; expiryDate: string; }
export interface Policy { policyId: number; title: string; description: string; policyType: string; basePremium: number; coverageAmount: number; durationMonths: number; isActive: boolean; }
export interface UserPolicy { id: number; userId: number; userName: string; policyId: number; policyTitle: string; policyType: string; basePremium: number; coverageAmount: number; durationMonths: number; finalPremium: number; discountApplied: number; purchaseDate: string; status: string; savedAmount: number; }
export interface PremiumBreakdown { policyId: number; policyTitle: string; policyType: string; basePremium: number; durationMonths: number; coverageAmount: number; userId: number; userPoints: number; badgesEarned: number; bestQuizScorePercent: number; appliedDiscounts: AppliedDiscount[]; totalDiscountPercent: number; discountedAmount: number; finalPremium: number; calculatedAt: string; }
export interface AppliedDiscount { ruleName: string; discountPercentage: number; reason: string; }
export interface DiscountRule { ruleId: number; ruleName: string; description: string; minQuizScorePercent: number; minUserPoints: number; minBadgesEarned: number; discountPercentage: number; applicablePolicyType: string | null; isActive: boolean; }
