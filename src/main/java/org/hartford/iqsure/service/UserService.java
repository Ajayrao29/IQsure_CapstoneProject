/*
 * ============================================================================
 * FILE: UserService.java | LOCATION: service/
 * PURPOSE: Business logic for user registration, login, profile, and leaderboard.
 *          This is where the actual work happens — controllers just forward requests here.
 *
 * KEY METHODS:
 *   register(dto)       → Creates a new user, hashes password, returns AuthResponse
 *   login(request)      → Validates email/password, returns AuthResponse
 *   getProfile(userId)  → Returns user profile DTO
 *   getLeaderboard()    → Returns top users sorted by points
 *   deleteUser(userId)  → Deletes a user (blocks admin deletion)
 *
 * CALLED BY: AuthController.java, UserController.java
 * USES: UserRepository, QuizAttemptRepository, PasswordEncoder
 * ============================================================================
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.auth.AuthRequest;       // → dto/auth/AuthRequest.java
import org.hartford.iqsure.dto.auth.AuthResponse;      // → dto/auth/AuthResponse.java
import org.hartford.iqsure.dto.request.UserRequestDTO;  // → dto/request/UserRequestDTO.java
import org.hartford.iqsure.dto.response.LeaderboardEntryDTO; // → dto/response/LeaderboardEntryDTO.java
import org.hartford.iqsure.dto.response.UserResponseDTO;     // → dto/response/UserResponseDTO.java
import org.hartford.iqsure.entity.User;                 // → entity/User.java
import org.hartford.iqsure.exception.BadRequestException;     // → exception/BadRequestException.java
import org.hartford.iqsure.exception.ResourceNotFoundException; // → exception/ResourceNotFoundException.java
import org.hartford.iqsure.repository.QuizAttemptRepository;  // → repository/QuizAttemptRepository.java
import org.hartford.iqsure.repository.UserRepository;         // → repository/UserRepository.java
import org.springframework.security.crypto.password.PasswordEncoder; // From config/SecurityConfig.java
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service                   // Marks this as a Spring service bean (business logic layer)
@RequiredArgsConstructor   // Lombok: auto-creates constructor to inject all 'final' fields
public class UserService {

    private final UserRepository userRepository;          // Talks to "users" DB table
    private final QuizAttemptRepository attemptRepository; // Needed for leaderboard stats
    private final PasswordEncoder passwordEncoder;         // BCrypt hasher from SecurityConfig

    // ── REGISTER: Create a new user account ──────────────────────────────
    // Called by: AuthController → POST /api/auth/register
    // Frontend: RegisterComponent (pages/register/register.ts)
    public AuthResponse register(UserRequestDTO dto) {
        // Step 1: Validate the input data
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }
        // Step 2: Check if email is already registered
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Step 3: Build User entity and save to database
        // All new registrations are ROLE_USER by default (not admin)
        User user = User.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword())) // HASH the password!
                .phone(dto.getPhone() != null ? dto.getPhone().trim() : null)
                .userPoints(0)              // New users start with 0 points
                .role(User.Role.ROLE_USER)  // Always a regular user
                .build();

        user = userRepository.save(user);   // Save to database → returns saved entity with generated ID

        // Step 4: Build and return response (stored in frontend localStorage)
        return AuthResponse.builder()
                .token("NO-AUTH")           // No JWT implemented — placeholder token
                .tokenType("None")
                .userId(user.getUserId())   // Frontend needs this for all API calls
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name()) // "ROLE_USER" — frontend uses this to show user/admin UI
                .build();
    }

    // ── LOGIN: Authenticate an existing user ─────────────────────────────
    // Called by: AuthController → POST /api/auth/login
    // Frontend: LoginComponent (pages/login/login.ts)
    public AuthResponse login(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        // Step 1: Find user by email (or throw error if not found)
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Step 2: Compare entered password with stored hash
        // passwordEncoder.matches() hashes the input and compares with stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Step 3: Return user info (stored in frontend localStorage)
        return AuthResponse.builder()
                .token("NO-AUTH")
                .tokenType("None")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ── GET PROFILE: Return user data by ID ──────────────────────────────
    // Called by: UserController → GET /api/v1/users/{userId}
    // Frontend: DashboardComponent, AchievementsComponent, SavingsCalculatorComponent
    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return toDTO(user);
    }

    // ── UPDATE PROFILE ───────────────────────────────────────────────────
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

    // ── DELETE USER (Admin only) ─────────────────────────────────────────
    // Called by: UserController → DELETE /api/v1/users/{userId}
    // Frontend: AdminUsersComponent (pages/admin/users/users.ts)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Safety check: prevent deleting admin users
        if (user.getRole() == User.Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot delete admin users");
        }
        
        userRepository.deleteById(userId);
    }

    // ── GET ALL USERS (Admin) ────────────────────────────────────────────
    // Called by: UserController → GET /api/v1/users
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    // ── LEADERBOARD: Top users ranked by points ──────────────────────────
    // Called by: UserController → GET /api/v1/users/leaderboard
    // Frontend: LeaderboardComponent (pages/leaderboard/leaderboard.ts)
    public List<LeaderboardEntryDTO> getLeaderboard() {
        List<User> users = userRepository.findTopUsersByPoints(); // Sorted by points DESC
        return java.util.stream.IntStream.range(0, users.size())
                .mapToObj(i -> LeaderboardEntryDTO.builder()
                        .rank(i + 1)    // Rank 1, 2, 3, ...
                        .userId(users.get(i).getUserId())
                        .name(users.get(i).getName())
                        .userPoints(users.get(i).getUserPoints())
                        .quizzesAttempted(attemptRepository.countByUser_UserId(users.get(i).getUserId()))
                        .build())
                .toList();
    }

    // ── HELPER: Convert User entity → UserResponseDTO ────────────────────
    // This prevents sending sensitive data (like password) to the frontend
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
