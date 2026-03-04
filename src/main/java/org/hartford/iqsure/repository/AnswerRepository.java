package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Get answer for a specific question
    Optional<Answer> findByQuestion_QuestionId(Long questionId);

    // Get all answers for questions in a quiz (used during scoring)
    List<Answer> findByQuestion_Quiz_QuizId(Long quizId);
}

