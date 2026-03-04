package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * The display text of the correct answer.
     * e.g. "Term Life"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    /**
     * Index of the correct option (0-based).
     * e.g. 0 → first option is correct
     */
    @Column(nullable = false)
    private Integer rightOption;
}
