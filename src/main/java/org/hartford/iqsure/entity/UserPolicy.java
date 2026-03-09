/*
 * ============================================================================
 * FILE: UserPolicy.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "user_policies" table. This is created when a user
 *          PURCHASES a policy. Stores the final premium after discounts.
 *
 * DATABASE TABLE: user_policies
 *   - id, user_id (FK), policy_id (FK), finalPremium, discountApplied,
 *     purchaseDate, status (ACTIVE/EXPIRED/CANCELLED)
 *
 * FLOW:
 *   1. User clicks "Purchase" on a policy (PoliciesComponent → frontend)
 *   2. Frontend calls POST /api/v1/users/{userId}/policies (api.service.ts)
 *   3. UserPolicyController → UserPolicyService.purchasePolicy()
 *   4. PremiumCalculationService calculates final price with gamification discounts
 *   5. A UserPolicy record is saved with the final discounted price
 *
 * USED BY:
 *   - UserPolicyController.java (controller/) → purchase & view endpoints
 *   - UserPolicyService.java (service/) → purchase logic
 *   - UserPolicyRepository.java (repository/) → database queries
 *   - MyPoliciesComponent (frontend: pages/my-policies/) → user sees purchased policies
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a user purchasing a specific insurance policy.
 * Links the existing User entity with the new Policy entity.
 * The finalPremium is computed by PremiumCalculationService using gamification data.
 */
@Entity
@Table(name = "user_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who purchased this policy.
     * References existing User entity — no modification to User needed.
     * → links to entity/User.java
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The policy template purchased.
     * → links to entity/Policy.java
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    /**
     * Final premium AFTER applying all applicable gamification discounts.
     * e.g., base $200, 15% discount → finalPremium = $170
     * (calculated by PremiumCalculationService.java)
     */
    @Column(nullable = false)
    private Double finalPremium;

    /**
     * Total discount percentage applied (e.g., 15.0 means 15% off).
     * This is stored for reference so user can see how much they saved.
     */
    @Column(nullable = false)
    private Double discountApplied;

    /**
     * When the policy was purchased.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime purchaseDate = LocalDateTime.now();

    // Current status of this purchased policy
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PolicyStatus status = PolicyStatus.ACTIVE;

    // Possible statuses for a purchased policy
    public enum PolicyStatus {
        ACTIVE,     // Policy is currently in effect
        EXPIRED,    // Policy duration has ended
        CANCELLED   // User cancelled the policy
    }
}
