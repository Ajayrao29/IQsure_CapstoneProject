/*
 * FILE: UserPolicyService.java | LOCATION: service/
 * PURPOSE: Handles policy purchase logic. When user buys a policy, this service:
 *          1. Calls PremiumCalculationService to get discounted price
 *          2. Creates a UserPolicy record with the final premium
 * CALLED BY: UserPolicyController.java
 * USES: UserRepository, PolicyRepository, UserPolicyRepository, PremiumCalculationService
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.UserPolicyRequestDTO;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PolicyRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPolicyService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final PremiumCalculationService premiumCalculationService;

    @Transactional
    public UserPolicyResponseDTO purchasePolicy(Long userId, UserPolicyRequestDTO dto, List<Long> selectedRewardIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Policy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + dto.getPolicyId()));

        if (!policy.getIsActive()) {
            throw new BadRequestException("Policy is not currently active: " + policy.getTitle());
        }

        // Calculate premium using gamification discounts + selected coupon rewards
        PremiumBreakdownDTO breakdown = premiumCalculationService.calculatePremium(userId, dto.getPolicyId(), selectedRewardIds);

        UserPolicy userPolicy = UserPolicy.builder()
                .user(user)
                .policy(policy)
                .finalPremium(breakdown.getFinalPremium())
                .discountApplied(breakdown.getTotalDiscountPercent())
                .purchaseDate(LocalDateTime.now())
                .status(UserPolicy.PolicyStatus.ACTIVE)
                .build();

        UserPolicyResponseDTO result = toDTO(userPolicyRepository.save(userPolicy));

        // Mark the selected rewards as used so they can't be applied to another policy
        premiumCalculationService.markRewardsAsUsed(selectedRewardIds);

        return result;
    }

    public List<UserPolicyResponseDTO> getUserPolicies(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return userPolicyRepository.findByUser_UserId(userId)
                .stream().map(this::toDTO).toList();
    }

    public UserPolicyResponseDTO getUserPolicyById(Long userId, Long userPolicyId) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));
        if (!up.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("This policy does not belong to the specified user.");
        }
        return toDTO(up);
    }

    private UserPolicyResponseDTO toDTO(UserPolicy up) {
        double saved = Math.round((up.getPolicy().getBasePremium() - up.getFinalPremium()) * 100.0) / 100.0;
        return UserPolicyResponseDTO.builder()
                .id(up.getId())
                .userId(up.getUser().getUserId())
                .userName(up.getUser().getName())
                .policyId(up.getPolicy().getPolicyId())
                .policyTitle(up.getPolicy().getTitle())
                .policyType(up.getPolicy().getPolicyType())
                .basePremium(up.getPolicy().getBasePremium())
                .coverageAmount(up.getPolicy().getCoverageAmount())
                .durationMonths(up.getPolicy().getDurationMonths())
                .finalPremium(up.getFinalPremium())
                .discountApplied(up.getDiscountApplied())
                .purchaseDate(up.getPurchaseDate())
                .status(up.getStatus())
                .savedAmount(saved)
                .build();
    }
}
