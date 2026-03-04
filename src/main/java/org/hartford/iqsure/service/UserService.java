package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.auth.AuthRequest;
import org.hartford.iqsure.dto.auth.AuthResponse;
import org.hartford.iqsure.dto.request.UserRequestDTO;
import org.hartford.iqsure.dto.response.LeaderboardEntryDTO;
import org.hartford.iqsure.dto.response.UserResponseDTO;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.QuizAttemptRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final QuizAttemptRepository attemptRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(UserRequestDTO dto) {
        // Validate input
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // All new registrations are ROLE_USER by default
        User user = User.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone() != null ? dto.getPhone().trim() : null)
                .userPoints(0)
                .role(User.Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        return AuthResponse.builder()
                .token("NO-AUTH")
                .tokenType("None")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        return AuthResponse.builder()
                .token("NO-AUTH")
                .tokenType("None")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return toDTO(user);
    }

    public UserResponseDTO updateProfile(Long userId, UserRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return toDTO(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Prevent deleting admin users
        if (user.getRole() == User.Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot delete admin users");
        }
        
        userRepository.deleteById(userId);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<LeaderboardEntryDTO> getLeaderboard() {
        List<User> users = userRepository.findTopUsersByPoints();
        List<LeaderboardEntryDTO> board = new ArrayList<>();
        int rank = 1;
        for (User u : users) {
            board.add(LeaderboardEntryDTO.builder()
                    .rank(rank)
                    .userId(u.getUserId())
                    .name(u.getName())
                    .userPoints(u.getUserPoints())
                    .quizzesAttempted(attemptRepository.countByUser_UserId(u.getUserId()))
                    .build());
            rank++;
        }
        return board;
    }

    private UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userPoints(user.getUserPoints())
                .role(user.getRole().name())
                .build();
    }
}
