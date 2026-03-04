package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmissionDTO {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    /**
     * Map of questionId → selected option index (0-based)
     * Example:
     * {
     *   "1": 2,
     *   "2": 0,
     *   "3": 3
     * }
     */
    @NotEmpty(message = "Answers cannot be empty")
    private Map<Long, Integer> answers;
}

