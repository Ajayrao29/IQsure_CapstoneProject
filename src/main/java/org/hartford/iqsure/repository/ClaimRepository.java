package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserPolicy_Id(Long userPolicyId);

    List<Claim> findByUserPolicy_User_UserId(Long userId);

    List<Claim> findByStatus(ClaimStatus status);
}