package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Minimum points required to earn this badge.
     * e.g. 100 points → "Bronze Learner"
     */
    @Column(nullable = false)
    private Integer reqPoints;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserBadge> userBadges = new ArrayList<>();
}
