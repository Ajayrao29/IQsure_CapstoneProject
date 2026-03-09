/*
 * ============================================================================
 * FILE: DataSeeder.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Automatically creates a default ADMIN user when the app starts
 *          for the FIRST TIME (when the database is empty).
 *          This ensures there's always an admin who can log in and set up
 *          quizzes, policies, badges, etc. through the admin panel.
 *
 * HOW IT WORKS:
 *   - @PostConstruct → Spring calls seedAdmin() after this bean is created
 *     and all dependencies (userRepository, passwordEncoder) are injected
 *   - Checks if any users exist in the database
 *   - If NO users exist → creates an admin with email "admin@iqsure.com"
 *   - If users already exist → does nothing (skips seeding)
 *
 * DEFAULT ADMIN CREDENTIALS:
 *   - Email:    admin@iqsure.com
 *   - Password: admin123
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Slf4j (Lombok) → Auto-creates a "log" variable for printing messages
 *   - @Component → Tells Spring: "create an instance of this class automatically"
 *   - @RequiredArgsConstructor (Lombok) → Auto-creates constructor for 'final' fields
 *   - @PostConstruct → Method runs once after bean initialization (replaces CommandLineRunner)
 *
 * CONNECTS TO:
 *   - UserRepository.java (repository/) → to check user count and save admin
 *   - SecurityConfig.java (config/) → provides the PasswordEncoder for hashing
 *   - User.java (entity/) → the User entity that gets saved to the "users" table
 * ============================================================================
 */
package org.hartford.iqsure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // This method runs automatically after the bean is created and dependencies are injected
    @PostConstruct
    public void seedAdmin() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@iqsure.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1234567890")
                    .role(User.Role.ROLE_ADMIN)
                    .userPoints(0)
                    .build());

            log.info("Admin user created: admin@iqsure.com / admin123");
        } else {
            log.info("Database already initialized");
        }
    }
}
