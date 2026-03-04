package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hartford.iqsure.entity.Quiz;

@Data
public class QuizRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Difficulty is required")
    private Quiz.Difficulty difficulty;
}

