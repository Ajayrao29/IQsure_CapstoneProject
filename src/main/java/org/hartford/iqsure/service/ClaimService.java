package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.ClaimActionDTO;
import org.hartford.iqsure.dto.request.ClaimRequestDTO;
import org.hartford.iqsure.dto.response.ClaimResponseDTO;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.ClaimStatus;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.ClaimRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserPolicyRepository userPolicyRepository;

    @Transactional
    public ClaimResponseDTO submitClaim(Long userId, ClaimRequestDTO dto) {
        UserPolicy userPolicy = userPolicyRepository.findById(dto.getUserPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("User policy not found: " + dto.getUserPolicyId()));

        if (!userPolicy.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You can only file claims for your own purchased policies.");
        }

        if (userPolicy.getStatus() != UserPolicy.PolicyStatus.ACTIVE) {
            throw new BadRequestException("Claims can only be filed for active policies.");
        }

        Claim claim = Claim.builder()
                .claimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .reason(dto.getReason())
                .description(dto.getDescription())
                .claimAmount(dto.getClaimAmount())
                .status(ClaimStatus.SUBMITTED)
                .claimDate(LocalDateTime.now())
                .documentUrl(dto.getDocumentUrl())
                .userPolicy(userPolicy)
                .build();

        return toDTO(claimRepository.save(claim));
    }

    public List<ClaimResponseDTO> getClaimsByUser(Long userId) {
        return claimRepository.findByUserPolicy_User_UserId(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ClaimResponseDTO> getClaimsByUserPolicy(Long userId, Long userPolicyId) {
        UserPolicy userPolicy = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("User policy not found: " + userPolicyId));

        if (!userPolicy.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("This policy does not belong to the specified user.");
        }

        return claimRepository.findByUserPolicy_Id(userPolicyId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ClaimResponseDTO> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ClaimResponseDTO approveClaim(Long claimId, ClaimActionDTO dto) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setAdminRemarks(dto != null ? dto.getAdminRemarks() : null);

        return toDTO(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponseDTO rejectClaim(Long claimId, ClaimActionDTO dto) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setAdminRemarks(dto != null ? dto.getAdminRemarks() : null);

        return toDTO(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponseDTO moveToReview(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        return toDTO(claimRepository.save(claim));
    }

    private ClaimResponseDTO toDTO(Claim claim) {
        return ClaimResponseDTO.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .reason(claim.getReason())
                .description(claim.getDescription())
                .claimAmount(claim.getClaimAmount())
                .status(claim.getStatus())
                .claimDate(claim.getClaimDate())
                .documentUrl(claim.getDocumentUrl())
                .adminRemarks(claim.getAdminRemarks())
                .fraudFlag(claim.getFraudFlag())
                .userPolicyId(claim.getUserPolicy().getId())
                .userId(claim.getUserPolicy().getUser().getUserId())
                .userName(claim.getUserPolicy().getUser().getName())
                .policyId(claim.getUserPolicy().getPolicy().getPolicyId())
                .policyTitle(claim.getUserPolicy().getPolicy().getTitle())
                .policyType(claim.getUserPolicy().getPolicy().getPolicyType().name())
                .build();
    }
}