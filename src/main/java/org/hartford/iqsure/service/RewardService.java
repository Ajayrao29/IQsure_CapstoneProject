package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.RewardRequestDTO;
import org.hartford.iqsure.dto.response.RewardResponseDTO;
import org.hartford.iqsure.entity.Reward;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserReward;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.RewardRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.hartford.iqsure.repository.UserRewardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final UserRewardRepository userRewardRepository;

    public RewardResponseDTO createReward(RewardRequestDTO dto) {
        Reward reward = Reward.builder()
                .rewardType(dto.getRewardType())
                .discountValue(dto.getDiscountValue())
                .expiryDate(dto.getExpiryDate())
                .build();
        return toDTO(rewardRepository.save(reward));
    }

    public List<RewardResponseDTO> getAllRewards() {
        return rewardRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<RewardResponseDTO> getActiveRewards() {
        return rewardRepository.findByExpiryDateAfter(LocalDate.now())
                .stream().map(this::toDTO).toList();
    }

    public List<RewardResponseDTO> getRewardsByUser(Long userId) {
        return userRewardRepository.findByUser_UserId(userId)
                .stream().map(ur -> toDTO(ur.getReward())).toList();
    }

    public RewardResponseDTO redeemReward(Long userId, Long rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found: " + rewardId));

        if (reward.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("This reward has expired");
        }

        if (userRewardRepository.existsByUser_UserIdAndReward_RewardId(userId, rewardId)) {
            throw new BadRequestException("You have already redeemed this reward");
        }

        UserReward userReward = UserReward.builder()
                .user(user)
                .reward(reward)
                .redeemedDate(LocalDateTime.now())
                .build();

        userRewardRepository.save(userReward);
        return toDTO(reward);
    }

    public void deleteReward(Long rewardId) {
        if (!rewardRepository.existsById(rewardId)) {
            throw new ResourceNotFoundException("Reward not found: " + rewardId);
        }
        rewardRepository.deleteById(rewardId);
    }

    private RewardResponseDTO toDTO(Reward r) {
        return RewardResponseDTO.builder()
                .rewardId(r.getRewardId())
                .rewardType(r.getRewardType())
                .discountValue(r.getDiscountValue())
                .expiryDate(r.getExpiryDate())
                .build();
    }
}
