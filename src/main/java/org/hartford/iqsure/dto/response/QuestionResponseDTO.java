package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponseDTO {

    private Long questionId;
    private Long quizId;
    private String text;

    /**
     * Options parsed from the comma-separated string into a list.
     * e.g. ["Term Life", "Whole Life", "Universal Life", "Variable Life"]
     */
    private List<String> options;

    // NOTE: correct answer is NOT included in this response
    // It is only used server-side during scoring
}

