package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId;

    @Column(nullable = false)
    private String rewardType;

    /**
     * Percentage or flat discount value.
     * e.g. 10.0 → 10% off
     */
    @Column(nullable = false)
    private Double discountValue;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserReward> userRewards = new ArrayList<>();
}
