package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    /**
     * Stores comma-separated options, e.g.:
     * "Term Life,Whole Life,Universal Life,Variable Life"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();
}
