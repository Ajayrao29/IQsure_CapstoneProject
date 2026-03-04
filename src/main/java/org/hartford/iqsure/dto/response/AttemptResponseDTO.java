package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttemptResponseDTO {

    private Long attemptId;
    private Long userId;
    private Long quizId;
    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private Integer pointsEarned;
    private LocalDateTime attemptDate;

    /**
     * Newly unlocked badges after this attempt (if any)
     */
    private java.util.List<BadgeResponseDTO> newBadgesUnlocked;
}

