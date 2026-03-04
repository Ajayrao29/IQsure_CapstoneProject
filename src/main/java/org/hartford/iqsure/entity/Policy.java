package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an insurance policy template available for purchase.
 * Admin creates policies; users can purchase them via UserPolicy.
 */
@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType policyType;

    /**
     * Base premium amount (in USD) before any gamification discounts.
     */
    @Column(nullable = false)
    private Double basePremium;

    /**
     * Maximum coverage amount for this policy (in USD).
     */
    @Column(nullable = false)
    private Double coverageAmount;

    /**
     * Duration of the policy in months.
     */
    @Column(nullable = false)
    private Integer durationMonths;

    /**
     * Whether this policy is currently offered.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserPolicy> userPolicies = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PremiumCalculationLog> premiumLogs = new ArrayList<>();

    public enum PolicyType {
        LIFE,
        HEALTH,
        MOTOR
    }
}

