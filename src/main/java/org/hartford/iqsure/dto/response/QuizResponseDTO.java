package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;
import org.hartford.iqsure.entity.Quiz;

@Data
@Builder
public class QuizResponseDTO {

    private Long quizId;
    private String title;
    private String category;
    private Quiz.Difficulty difficulty;
    private int totalQuestions;
}

