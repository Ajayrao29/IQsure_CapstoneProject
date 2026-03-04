package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {

    List<UserPolicy> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);
}

