/*
 * ============================================================================
 * FILE: QuizAttemptService.java | LOCATION: service/
 * PURPOSE: THE CORE GAMIFICATION ENGINE. Handles quiz submission, scoring,
 *          point awarding, and badge checking. This is where the magic happens!
 *
 * KEY METHOD — submitQuiz():
 *   1. Gets correct answers from DB for the quiz
 *   2. Compares user's answers with correct answers → calculates score
 *   3. If FIRST attempt on this quiz → awards points (score × 10)
 *   4. Updates user's total points in the database
 *   5. Checks if user qualifies for any new badges (BadgeService)
 *   6. Returns result with score, percentage, points, and new badges
 *
 * CALLED BY: AttemptController.java
 * USES: QuizAttemptRepository, UserRepository, QuizRepository, AnswerRepository, BadgeService
 * ============================================================================
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.QuizSubmissionDTO;
import org.hartford.iqsure.dto.response.AttemptResponseDTO;
import org.hartford.iqsure.dto.response.BadgeResponseDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final BadgeService badgeService;

    @Transactional
    public AttemptResponseDTO submitQuiz(Long userId, QuizSubmissionDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + dto.getQuizId()));

        // Get correct answers for this quiz
        Map<Long, Integer> correctAnswers = answerRepository
                .findByQuestion_Quiz_QuizId(dto.getQuizId())
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getQuestion().getQuestionId(),
                        Answer::getRightOption
                ));

        // Count correct answers
        int score = 0;
        for (Map.Entry<Long, Integer> entry : dto.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            Integer selected = entry.getValue();
            if (correctAnswers.containsKey(questionId) && correctAnswers.get(questionId).equals(selected)) {
                score++;
            }
        }

        int totalQuestions = (int) questionRepository.countByQuiz_QuizId(dto.getQuizId());

        // Award points only on first attempt
        boolean isFirstAttempt = attemptRepository
                .findFirstByUser_UserIdAndQuiz_QuizId(userId, dto.getQuizId())
                .isEmpty();

        int pointsEarned = 0;
        if (isFirstAttempt) {
            pointsEarned = score * 10;
            user.setUserPoints(user.getUserPoints() + pointsEarned);
            userRepository.save(user);
        }

        // Save the attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .score(score)
                .totalQuestions(totalQuestions)
                .pointsEarned(pointsEarned)
                .attemptDate(LocalDateTime.now())
                .build();

        attempt = attemptRepository.save(attempt);

        // Check for newly unlocked badges
        List<BadgeResponseDTO> newBadges = isFirstAttempt
                ? badgeService.checkAndAwardBadges(userId)
                : List.of();

        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0;

        return AttemptResponseDTO.builder()
                .attemptId(attempt.getAttemptId())
                .userId(userId)
                .quizId(dto.getQuizId())
                .quizTitle(quiz.getTitle())
                .score(score)
                .totalQuestions(totalQuestions)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .pointsEarned(pointsEarned)
                .attemptDate(attempt.getAttemptDate())
                .newBadgesUnlocked(newBadges)
                .build();
    }

    public List<AttemptResponseDTO> getAttemptsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return attemptRepository.findByUser_UserIdOrderByAttemptDateDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    public AttemptResponseDTO getAttemptById(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));
        return toDTO(attempt);
    }

    private AttemptResponseDTO toDTO(QuizAttempt a) {
        double pct = a.getTotalQuestions() > 0
                ? ((double) a.getScore() / a.getTotalQuestions()) * 100 : 0;
        return AttemptResponseDTO.builder()
                .attemptId(a.getAttemptId())
                .userId(a.getUser().getUserId())
                .quizId(a.getQuiz().getQuizId())
                .quizTitle(a.getQuiz().getTitle())
                .score(a.getScore())
                .totalQuestions(a.getTotalQuestions())
                .percentage(Math.round(pct * 100.0) / 100.0)
                .pointsEarned(a.getPointsEarned())
                .attemptDate(a.getAttemptDate())
                .newBadgesUnlocked(List.of())
                .build();
    }
}
