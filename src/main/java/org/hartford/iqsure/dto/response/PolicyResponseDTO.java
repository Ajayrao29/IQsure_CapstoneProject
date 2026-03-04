package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;

/**
 * Response DTO for a Policy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponseDTO {

    private Long policyId;
    private String title;
    private String description;
    private Policy.PolicyType policyType;
    private Double basePremium;
    private Double coverageAmount;
    private Integer durationMonths;
    private Boolean isActive;
}

