package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimRequestDTO {

    @NotNull(message = "User policy id is required")
    private Long userPolicyId;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String description;

    @NotNull(message = "Claim amount is required")
    @Min(value = 1, message = "Claim amount must be greater than 0")
    private Double claimAmount;

    private String documentUrl;
}