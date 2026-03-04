package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PremiumCalculationService {

    // Total discount is capped at 50% no matter how many rules match
    private static final double MAX_DISCOUNT_CAP = 50.0;

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final DiscountRuleRepository discountRuleRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PremiumCalculationLogRepository logRepository;

    @Transactional
    public PremiumBreakdownDTO calculatePremium(Long userId, Long policyId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + policyId));

        // Get user's gamification stats
        int userPoints = user.getUserPoints();
        int badgeCount = userBadgeRepository.findByUser_UserId(userId).size();
        Double rawBestScore = quizAttemptRepository.findBestScorePercentByUserId(userId);
        double bestQuizScore = rawBestScore != null ? rawBestScore : 0.0;

        // Check which discount rules apply
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

        // Cap total discount at 50%
        if (totalDiscount > MAX_DISCOUNT_CAP) {
            totalDiscount = MAX_DISCOUNT_CAP;
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

    // Returns true only if the user meets ALL conditions of the rule
    private boolean isRuleSatisfied(DiscountRule rule, int points, int badges, double score) {
        if (rule.getMinUserPoints() > 0 && points < rule.getMinUserPoints()) return false;
        if (rule.getMinBadgesEarned() > 0 && badges < rule.getMinBadgesEarned()) return false;
        if (rule.getMinQuizScorePercent() > 0 && score < rule.getMinQuizScorePercent()) return false;
        return true;
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
