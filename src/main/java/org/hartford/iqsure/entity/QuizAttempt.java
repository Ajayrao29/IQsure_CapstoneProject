package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer score;

    /**
     * Total questions in the quiz at time of attempt.
     * Used to calculate percentage.
     */
    @Column(nullable = false)
    private Integer totalQuestions;

    /**
     * Points awarded for this attempt.
     * Points are awarded only on the FIRST attempt per quiz.
     */
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime attemptDate = LocalDateTime.now();
}
