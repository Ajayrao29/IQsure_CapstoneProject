package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    // Find all badges the user qualifies for based on their current points
    List<Badge> findByReqPointsLessThanEqual(Integer points);
}

