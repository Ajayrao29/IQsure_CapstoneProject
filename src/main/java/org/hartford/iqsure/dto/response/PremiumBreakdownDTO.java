package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detailed premium calculation breakdown.
 * Returned when a user requests a premium preview before purchasing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumBreakdownDTO {

    // Policy info
    private Long policyId;
    private String policyTitle;
    private Policy.PolicyType policyType;
    private Double basePremium;
    private Integer durationMonths;
    private Double coverageAmount;

    // User engagement snapshot
    private Long userId;
    private Integer userPoints;
    private Integer badgesEarned;
    private Double bestQuizScorePercent;

    // Discount breakdown
    private List<AppliedDiscountDTO> appliedDiscounts;
    private Double totalDiscountPercent;
    private Double discountedAmount;

    // Final result
    private Double finalPremium;
    private LocalDateTime calculatedAt;

    /**
     * Represents a single discount rule that was matched.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppliedDiscountDTO {
        private String ruleName;
        private Double discountPercentage;
        private String reason;
    }
}

