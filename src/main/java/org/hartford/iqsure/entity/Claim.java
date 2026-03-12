package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(nullable = false)
    private String reason;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double claimAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime claimDate = LocalDateTime.now();

    private String documentUrl;

    @Column(length = 1000)
    private String adminRemarks;

    @Column(nullable = false)
    @Builder.Default
    private Boolean fraudFlag = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_policy_id", nullable = false)
    private UserPolicy userPolicy;
}