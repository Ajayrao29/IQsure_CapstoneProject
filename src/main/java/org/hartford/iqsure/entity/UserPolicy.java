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
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The policy template purchased.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    /**
     * Final premium after applying all applicable gamification discounts.
     */
    @Column(nullable = false)
    private Double finalPremium;

    /**
     * Total discount percentage applied (e.g., 15.0 means 15% off).
     */
    @Column(nullable = false)
    private Double discountApplied;

    /**
     * When the policy was purchased.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PolicyStatus status = PolicyStatus.ACTIVE;

    public enum PolicyStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED
    }
}

