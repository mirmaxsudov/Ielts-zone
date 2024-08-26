package uz.ieltszone.zonelifeservice.service.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.dto.response.AttachmentResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponseWithSize;

import java.util.List;

@Component
public class RewardMapper {
    @Value("${get.img.url}")
    private static String GET_IMG_URL;

    public RewardResponse toResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .createdAt(reward.getCreatedAt())
                .rewardName(reward.getRewardName())
                .description(reward.getDescription())
                .attachmentResponse(
                        AttachmentResponse.builder()
                                .attachmentId(reward.getImageId())
                                .url(GET_IMG_URL + "/" + reward.getImageId())
                                .build()
                )
                .build();
    }

    public RewardResponseWithSize toResponseWithSize(List<Reward> rewards) {
        return RewardResponseWithSize.builder()
                .size(rewards.size())
                .rewardResponses(
                        rewards.stream()
                                .map(this::toResponse)
                                .toList()
                )
                .build();
    }
}