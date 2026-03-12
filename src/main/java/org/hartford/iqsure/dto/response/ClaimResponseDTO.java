package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;
import org.hartford.iqsure.entity.ClaimStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimResponseDTO {
    private Long id;
    private String claimNumber;
    private String reason;
    private String description;
    private Double claimAmount;
    private ClaimStatus status;
    private LocalDateTime claimDate;
    private String documentUrl;
    private String adminRemarks;
    private Boolean fraudFlag;

    private Long userPolicyId;
    private Long userId;
    private String userName;

    private Long policyId;
    private String policyTitle;
    private String policyType;
}