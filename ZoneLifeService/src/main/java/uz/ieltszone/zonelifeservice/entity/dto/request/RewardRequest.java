package uz.ieltszone.zonelifeservice.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequest {
    @NotNull(message = "Who created the reward?")
    private Long createdById;
    @NotNull(message = "What is the name of the reward?")
    private String rewardName;
    private String description;
    private Long imageId;
}