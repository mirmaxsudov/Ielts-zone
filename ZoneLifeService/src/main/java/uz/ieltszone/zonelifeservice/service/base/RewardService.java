package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.request.RewardRequestUpdate;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface RewardService {
    ApiResponse<?> save(RewardRequest rewardRequest);
    ApiResponse<RewardResponseWithSize> getAll();

    ApiResponse<RewardResponse> getById(Long rewardId);

    ApiResponse<Long> getSize();

    RewardResponseWithSize getAllByTeacher(Long teacherId);

    ApiResponse<?> update(Long rewardId, RewardRequestUpdate updateRequest);
}