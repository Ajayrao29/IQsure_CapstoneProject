package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Audit log for every premium calculation performed.
 * Stores user engagement snapshot and which discount rules were applied.
 */
@Entity
@Table(name = "premium_calculation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumCalculationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    /**
     * User for whom premium was calculated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Policy for which premium was calculated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    /**
     * Base premium of the policy before discounts.
     */
    @Column(nullable = false)
    private Double basePremium;

    /**
     * Total discount percentage applied (sum of all matching rules, capped at 50%).
     */
    @Column(nullable = false)
    private Double totalDiscountPercent;

    /**
     * Final premium after applying discounts.
     */
    @Column(nullable = false)
    private Double finalPremium;

    /**
     * Snapshot of user points at time of calculation.
     */
    @Column(nullable = false)
    private Integer userPointsSnapshot;

    /**
     * Snapshot of badge count at time of calculation.
     */
    @Column(nullable = false)
    private Integer badgeCountSnapshot;

    /**
     * Snapshot of best quiz score percentage at time of calculation.
     */
    @Column(nullable = false)
    private Double bestQuizScoreSnapshot;

    /**
     * Comma-separated list of rule names that were applied.
     */
    @Column(columnDefinition = "TEXT")
    private String appliedRuleNames;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();
}

