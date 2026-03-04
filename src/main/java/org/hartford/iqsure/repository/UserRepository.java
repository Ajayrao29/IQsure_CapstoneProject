package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by Spring Security to load user for login
    Optional<User> findByEmail(String email);

    // Check if email already registered
    boolean existsByEmail(String email);

    // Leaderboard — top N users ordered by points descending
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_USER' ORDER BY u.userPoints DESC")
    List<User> findTopUsersByPoints();
}
