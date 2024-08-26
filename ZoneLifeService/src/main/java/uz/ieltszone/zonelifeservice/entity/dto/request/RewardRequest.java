package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequest {
    private Long createdById;
    private String rewardName;
    private String description;
    private Long imageId;
}