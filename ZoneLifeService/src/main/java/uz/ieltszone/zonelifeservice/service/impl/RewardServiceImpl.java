package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.exceptions.CustomNotFoundException;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.RewardRepository;
import uz.ieltszone.zonelifeservice.repository.RewardTeachersRepository;
import uz.ieltszone.zonelifeservice.service.base.RewardService;
import uz.ieltszone.zonelifeservice.service.feign.FileFeign;
import uz.ieltszone.zonelifeservice.service.mapper.RewardMapper;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;
    private final FileFeign fileFeign;
    private final RewardTeachersRepository rewardTeachersRepository;
    private final RewardMapper rewardMapper;

    @Override
    @Modifying
    @Transactional
    public ApiResponse<?> save(RewardRequest rewardRequest) {
        if (fileFeign.existsAttachment(rewardRequest.getImageId()).getData())
            throw new CustomNotFoundException("File not found");

        Reward reward = new Reward();
        reward.setRewardName(rewardRequest.getRewardName());
        reward.setDescription(rewardRequest.getDescription());
        reward.setCreatedBy(rewardRequest.getCreatedById());
        reward.setImageId(rewardRequest.getImageId());
        reward.setCreatedAt(LocalDate.now());

        rewardRepository.save(reward);

        return new ApiResponse<>()
                .success("Successfully saved");
    }

    @Override
    public ApiResponse<RewardResponseWithSize> getAll() {
        List<Reward> rewards = rewardRepository.findAll();

        return new ApiResponse<RewardResponseWithSize>()
                .success(
                        "Successfully fetched",
                        rewardMapper.toResponseWithSize(rewards)
                );
    }

    private Reward getByIdFotBackend(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(
                        () -> new CustomNotFoundException("Reward not found!")
                );
    }

    @Override
    public ApiResponse<RewardResponse> getById(Long rewardId) {
        Reward reward = getByIdFotBackend(rewardId);

        return new ApiResponse<RewardResponse>()
                .success(
                        "Successfully fetched",
                        rewardMapper.toResponse(reward)
                );
    }

    @Override
    public ApiResponse<Long> getSize() {
        return new ApiResponse<Long>()
                .success(
                        "Successfully fetched",
                        rewardRepository.count()
                );
    }

    @Override
    public RewardResponseWithSize getAllByTeacher(Long teacherId) {
        List<Reward> rewards = rewardTeachersRepository.findAllByTeacherId(teacherId);

        if (rewards.isEmpty())
            throw new CustomNotFoundException("Rewards not found for this teacher");

        return rewardMapper.toResponseWithSize(rewards);
    }
}