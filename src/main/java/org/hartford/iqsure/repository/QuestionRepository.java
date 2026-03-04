package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Get all questions for a given quiz
    List<Question> findByQuiz_QuizId(Long quizId);

    // Count questions in a quiz (used to calculate score %)
    long countByQuiz_QuizId(Long quizId);
}

