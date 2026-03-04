package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request body for purchasing a policy.
 * The finalPremium is calculated server-side — user only provides policyId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyRequestDTO {

    @NotNull(message = "Policy ID is required")
    private Long policyId;
}

