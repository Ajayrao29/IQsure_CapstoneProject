/*
 * ============================================================================
 * FILE: PremiumCalculationService.java | LOCATION: service/
 * PURPOSE: THE PREMIUM DISCOUNT ENGINE. This is the most important service in the app.
 *          It calculates personalized insurance premiums by evaluating gamification
 *          discount rules against a user's engagement data.
 *
 * HOW IT WORKS (calculatePremium method):
 *   1. Load user's gamification stats: points, badge count, best quiz score
 *   2. Find all active discount rules that apply to this policy type
 *   3. For each rule, check if user meets ALL conditions (points, badges, score)
 *   4. Sum up all matching rule discounts (capped at 50% max from AppConfig)
 *   5. Calculate: finalPremium = basePremium - (basePremium × totalDiscount%)
 *   6. Save an audit log (PremiumCalculationLog) for transparency
 *   7. Return a detailed breakdown showing everything
 *
 * CALLED BY: UserPolicyController.java (preview), UserPolicyService.java (purchase)
 * USES: UserRepository, PolicyRepository, DiscountRuleRepository, UserBadgeRepository,
 *       QuizAttemptRepository, PremiumCalculationLogRepository, AppConfig
 * ============================================================================
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.config.AppConfig;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.PremiumCalculationLogResponseDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PremiumCalculationService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final DiscountRuleRepository discountRuleRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRewardRepository userRewardRepository;
    private final PremiumCalculationLogRepository logRepository;
    private final AppConfig appConfig;

    @Transactional
    public PremiumBreakdownDTO calculatePremium(Long userId, Long policyId, List<Long> selectedRewardIds) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + policyId));

        // Get user's gamification stats
        int userPoints = user.getUserPoints();
        int badgeCount = userBadgeRepository.findByUser_UserId(userId).size();
        Double rawBestScore = quizAttemptRepository.findBestScorePercentByUserId(userId);
        double bestQuizScore = rawBestScore != null ? rawBestScore : 0.0;

        // Check which automatic discount rules apply
        List<DiscountRule> rules = discountRuleRepository.findActiveRulesForPolicyType(policy.getPolicyType());
        List<PremiumBreakdownDTO.AppliedDiscountDTO> applied = new ArrayList<>();
        double totalDiscount = 0.0;

        for (DiscountRule rule : rules) {
            if (isRuleSatisfied(rule, userPoints, badgeCount, bestQuizScore)) {
                applied.add(PremiumBreakdownDTO.AppliedDiscountDTO.builder()
                        .ruleName(rule.getRuleName())
                        .discountPercentage(rule.getDiscountPercentage())
                        .reason(buildReason(rule))
                        .build());
                totalDiscount += rule.getDiscountPercentage();
            }
        }
        
        // Apply only the SELECTED rewards the user explicitly chose as coupons
        if (selectedRewardIds != null && !selectedRewardIds.isEmpty()) {
            Set<Long> selectedSet = Set.copyOf(selectedRewardIds);
            List<UserReward> allUserRewards = userRewardRepository.findByUser_UserIdAndUsedFalse(userId);
            for (UserReward ur : allUserRewards) {
                if (!selectedSet.contains(ur.getId())) continue;
                Reward r = ur.getReward();
                if (r.getExpiryDate().isAfter(java.time.LocalDate.now()) || r.getExpiryDate().isEqual(java.time.LocalDate.now())) {
                    applied.add(PremiumBreakdownDTO.AppliedDiscountDTO.builder()
                            .ruleName(r.getRewardType())
                            .discountPercentage(r.getDiscountValue())
                            .reason("Coupon applied on purchase")
                            .build());
                    totalDiscount += r.getDiscountValue();
                }
            }
        }

        // Cap total discount at configured max
        if (totalDiscount > appConfig.getMaxDiscountCap()) {
            totalDiscount = appConfig.getMaxDiscountCap();
        }

        double basePremium = policy.getBasePremium();
        double discountedAmount = Math.round(basePremium * (totalDiscount / 100.0) * 100.0) / 100.0;
        double finalPremium = Math.round((basePremium - discountedAmount) * 100.0) / 100.0;
        double roundedBestScore = Math.round(bestQuizScore * 100.0) / 100.0;

        // Save audit log
        String ruleNames = applied.stream()
                .map(PremiumBreakdownDTO.AppliedDiscountDTO::getRuleName)
                .collect(Collectors.joining(", "));

        logRepository.save(PremiumCalculationLog.builder()
                .user(user)
                .policy(policy)
                .basePremium(basePremium)
                .totalDiscountPercent(totalDiscount)
                .finalPremium(finalPremium)
                .userPointsSnapshot(userPoints)
                .badgeCountSnapshot(badgeCount)
                .bestQuizScoreSnapshot(roundedBestScore)
                .appliedRuleNames(ruleNames.isEmpty() ? "None" : ruleNames)
                .calculatedAt(LocalDateTime.now())
                .build());

        return PremiumBreakdownDTO.builder()
                .policyId(policy.getPolicyId())
                .policyTitle(policy.getTitle())
                .policyType(policy.getPolicyType())
                .basePremium(basePremium)
                .durationMonths(policy.getDurationMonths())
                .coverageAmount(policy.getCoverageAmount())
                .userId(userId)
                .userPoints(userPoints)
                .badgesEarned(badgeCount)
                .bestQuizScorePercent(roundedBestScore)
                .appliedDiscounts(applied)
                .totalDiscountPercent(totalDiscount)
                .discountedAmount(discountedAmount)
                .finalPremium(finalPremium)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    public List<PremiumCalculationLogResponseDTO> getLogsForUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found: " + userId);
        return logRepository.findByUser_UserIdOrderByCalculatedAtDesc(userId)
                .stream().map(this::toLogDTO).toList();
    }

    public List<PremiumCalculationLogResponseDTO> getLogsForUserAndPolicy(Long userId, Long policyId) {
        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found: " + userId);
        if (!policyRepository.existsById(policyId))
            throw new ResourceNotFoundException("Policy not found: " + policyId);
        return logRepository.findByUser_UserIdAndPolicy_PolicyIdOrderByCalculatedAtDesc(userId, policyId)
                .stream().map(this::toLogDTO).toList();
    }

    // Mark selected rewards as used after a successful purchase so they can't be reused
    @Transactional
    public void markRewardsAsUsed(List<Long> userRewardIds) {
        if (userRewardIds == null || userRewardIds.isEmpty()) return;
        userRewardRepository.findAllById(userRewardIds).forEach(ur -> {
            ur.setUsed(true);
            userRewardRepository.save(ur);
        });
    }

    // Get user's available (unused, unexpired) redeemed rewards for the coupon picker
    public List<UserReward> getAvailableRewardsForUser(Long userId) {
        return userRewardRepository.findByUser_UserIdAndUsedFalse(userId)
                .stream()
                .filter(ur -> !ur.getReward().getExpiryDate().isBefore(java.time.LocalDate.now()))
                .toList();
    }

    // Returns true only if the user meets ALL conditions of the rule
    private boolean isRuleSatisfied(DiscountRule rule, int points, int badges, double score) {
        return (rule.getMinUserPoints() <= 0 || points >= rule.getMinUserPoints()) &&
               (rule.getMinBadgesEarned() <= 0 || badges >= rule.getMinBadgesEarned()) &&
               (rule.getMinQuizScorePercent() <= 0 || score >= rule.getMinQuizScorePercent());
    }

    // Builds a human-readable reason string for why the discount was applied
    private String buildReason(DiscountRule rule) {
        List<String> conditions = new ArrayList<>();
        if (rule.getMinUserPoints() > 0)       conditions.add("points >= " + rule.getMinUserPoints());
        if (rule.getMinBadgesEarned() > 0)     conditions.add("badges >= " + rule.getMinBadgesEarned());
        if (rule.getMinQuizScorePercent() > 0) conditions.add("quiz score >= " + rule.getMinQuizScorePercent() + "%");
        return conditions.isEmpty() ? "Always applied" : String.join(", ", conditions);
    }

    private PremiumCalculationLogResponseDTO toLogDTO(PremiumCalculationLog l) {
        return PremiumCalculationLogResponseDTO.builder()
                .logId(l.getLogId())
                .userId(l.getUser().getUserId())
                .policyId(l.getPolicy().getPolicyId())
                .policyTitle(l.getPolicy().getTitle())
                .basePremium(l.getBasePremium())
                .totalDiscountPercent(l.getTotalDiscountPercent())
                .finalPremium(l.getFinalPremium())
                .userPointsSnapshot(l.getUserPointsSnapshot())
                .badgeCountSnapshot(l.getBadgeCountSnapshot())
                .bestQuizScoreSnapshot(l.getBestQuizScoreSnapshot())
                .appliedRuleNames(l.getAppliedRuleNames())
                .calculatedAt(l.getCalculatedAt())
                .build();
    }
}
