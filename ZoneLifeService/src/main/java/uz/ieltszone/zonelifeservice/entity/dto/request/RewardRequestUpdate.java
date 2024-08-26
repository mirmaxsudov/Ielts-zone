package uz.ieltszone.zonelifeservice.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequestUpdate {
    @NotNull(message = "rewardName must not be null")
    private String rewardName;
    private String description;
    private Long imageId;
}