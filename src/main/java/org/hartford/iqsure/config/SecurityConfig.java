/*
 * ============================================================================
 * FILE: SecurityConfig.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Configures password encryption for the application.
 *          When a user registers, their password is HASHED (scrambled) using
 *          BCrypt before saving to the database. This means even if someone
 *          sees the database, they can't read the actual passwords.
 *
 * HOW IT WORKS:
 *   - Creates a PasswordEncoder bean using BCrypt algorithm
 *   - BCrypt is a one-way hash: you can encode "admin123" → "$2a$10$xYz..."
 *     but you CANNOT reverse "$2a$10$xYz..." back to "admin123"
 *   - During login, Spring compares the hashed version of the entered password
 *     with the stored hash
 *
 * USED BY:
 *   - DataSeeder.java (config/) → hashes the default admin password
 *   - UserService.java (service/) → hashes passwords during registration & login
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Configuration → Tells Spring: "this class contains bean definitions"
 *   - @Bean → Tells Spring: "create this object and make it available for injection"
 * ============================================================================
 */
package org.hartford.iqsure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration  // Marks this class as a source of Spring bean definitions
public class SecurityConfig {

    // @Bean means Spring will create this object and store it.
    // Anywhere that needs a PasswordEncoder, Spring will inject THIS instance.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder is an industry-standard password hashing algorithm.
        // It automatically adds a "salt" (random data) to make each hash unique.
        return new BCryptPasswordEncoder();
    }
}
