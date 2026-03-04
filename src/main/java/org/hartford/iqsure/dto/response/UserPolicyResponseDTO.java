package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.UserPolicy;

import java.time.LocalDateTime;

/**
 * Response DTO for a user's purchased policy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyResponseDTO {

    private Long id;
    private Long userId;
    private String userName;

    // Policy summary
    private Long policyId;
    private String policyTitle;
    private Policy.PolicyType policyType;
    private Double basePremium;
    private Double coverageAmount;
    private Integer durationMonths;

    // Purchase details
    private Double finalPremium;
    private Double discountApplied;
    private LocalDateTime purchaseDate;
    private UserPolicy.PolicyStatus status;

    /**
     * Amount saved due to gamification discounts.
     */
    private Double savedAmount;
}

