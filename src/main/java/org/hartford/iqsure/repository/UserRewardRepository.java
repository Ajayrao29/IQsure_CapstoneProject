package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {

    // All rewards redeemed by a user
    List<UserReward> findByUser_UserId(Long userId);

    // Check if user already redeemed a specific reward
    boolean existsByUser_UserIdAndReward_RewardId(Long userId, Long rewardId);
}

