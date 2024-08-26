package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.RewardTeachers;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequestUpdate;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.exceptions.CustomNotFoundException;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.RewardRepository;
import uz.ieltszone.zonelifeservice.repository.RewardTeachersRepository;
import uz.ieltszone.zonelifeservice.service.base.RewardService;
import uz.ieltszone.zonelifeservice.service.base.TeacherService;
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
        reward.setIsDeleted(false);

        rewardRepository.save(reward);

        return new ApiResponse<>()
                .success("Successfully saved");
    }

    @Override
    public ApiResponse<RewardResponseWithSize> getAll() {
        List<Reward> rewards = rewardRepository.findAll()
                .stream()
                .filter(reward -> !reward.getIsDeleted())
                .toList();

        return new ApiResponse<RewardResponseWithSize>()
                .success(
                        "Successfully fetched",
                        rewardMapper.toResponseWithSize(rewards)
                );
    }

    @Override
    public ApiResponse<RewardResponse> getById(Long rewardId) {
        Reward reward = getByIdForBackend(rewardId);

        return new ApiResponse<RewardResponse>()
                .success(
                        "Successfully fetched",
                        rewardMapper.toResponse(reward)
                );
    }

    @Override
    public Reward getByIdForBackend(Long rewardId) {
        return rewardRepository.findById(rewardId)
                .orElseThrow(
                        () -> new CustomNotFoundException("Reward not found!")
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

    @Override
    public ApiResponse<?> update(Long rewardId, RewardRequestUpdate updateRequest) {
        Reward reward = getByIdForBackend(rewardId);

        if (fileFeign.existsAttachment(updateRequest.getImageId()).getData())
            throw new CustomNotFoundException("File not found");

        reward.setRewardName(updateRequest.getRewardName());
        reward.setDescription(updateRequest.getDescription());
        reward.setImageId(updateRequest.getImageId());
        rewardRepository.save(reward);

        return new ApiResponse<>()
                .success("Successfully updated");
    }

    private boolean existsReward(Long rewardId) {
        return rewardRepository.existsById(rewardId);
    }

    @Override
    @Modifying
    public ResponseEntity<ApiResponse<?>> setReward(Long teacherId, Long rewardId) {
        if (!existsReward(rewardId))
            throw new CustomNotFoundException("Reward not found");

        RewardTeachers rewardTeachers = new RewardTeachers();
        rewardTeachers.setRewardId(rewardId);
        rewardTeachers.setTeacherId(teacherId);

        rewardTeachersRepository.save(rewardTeachers);
        return ResponseEntity.ok(new ApiResponse<>().success("Successfully set reward"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteRewardFromTeacher(Long teacherId, Long rewardId) {
        rewardTeachersRepository.deleteByTeacherId(teacherId, rewardId);
        return ResponseEntity.ok(new ApiResponse<>().success("Successfully deleted reward from teacher"));
    }

    @Override
    @Transactional
    public void delete(Long rewardId) {
        Reward reward = getByIdForBackend(rewardId);
        reward.setIsDeleted(true);
        rewardRepository.save(reward);
    }

    @Override
    public ApiResponse<RewardResponseWithSize> getAllByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Reward> pages = rewardRepository.findAll(pageable);

        if (pages.isEmpty())
            throw new CustomNotFoundException("Rewards not found");

        return new ApiResponse<RewardResponseWithSize>()
                .success(
                        "Successfully fetched",
                        rewardMapper.toResponseWithSize(
                                pages.getContent()
                        )
                );
    }
}