package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Admin-configurable discount rules based on user gamification engagement.
 *
 * Each rule defines conditions (minimum quiz score, points, badges) and
 * a discount percentage to award when ALL conditions are met.
 *
 * The rule can optionally be scoped to a specific PolicyType or apply to ALL policies.
 */
@Entity
@Table(name = "discount_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Column(nullable = false, unique = true)
    private String ruleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Minimum quiz score percentage required (e.g., 80.0 means user must score >= 80%).
     * Set to 0 to ignore this condition.
     */
    @Builder.Default
    @Column(nullable = false)
    private Double minQuizScorePercent = 0.0;

    /**
     * Minimum total user points required.
     * Set to 0 to ignore this condition.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer minUserPoints = 0;

    /**
     * Minimum number of badges the user must have earned.
     * Set to 0 to ignore this condition.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer minBadgesEarned = 0;

    /**
     * Discount percentage to apply when this rule's conditions are all met.
     * e.g., 10.0 means 10% off the base premium.
     */
    @Column(nullable = false)
    private Double discountPercentage;

    /**
     * Which policy type this rule applies to.
     * NULL means it applies to ALL policy types.
     */
    @Enumerated(EnumType.STRING)
    private Policy.PolicyType applicablePolicyType;

    /**
     * Whether this rule is currently active.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}

