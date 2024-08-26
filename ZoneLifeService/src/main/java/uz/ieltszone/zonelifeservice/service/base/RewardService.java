package uz.ieltszone.zonelifeservice.service.base;

import org.springframework.http.ResponseEntity;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequestUpdate;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface RewardService {
    ApiResponse<?> save(RewardRequest rewardRequest);

    ApiResponse<RewardResponseWithSize> getAll();

    ApiResponse<RewardResponse> getById(Long rewardId);

    Reward getByIdForBackend(Long rewardId);

    ApiResponse<Long> getSize();

    RewardResponseWithSize getAllByTeacher(Long teacherId);

    ApiResponse<?> update(Long rewardId, RewardRequestUpdate updateRequest);

    ResponseEntity<ApiResponse<?>> setReward(Long teacherId, Long rewardId);

    ResponseEntity<ApiResponse<?>> deleteRewardFromTeacher(Long teacherId, Long rewardId);

    void delete(Long rewardId);

    ApiResponse<RewardResponseWithSize> getAllByPage(int page, int size);
}