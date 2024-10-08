package uz.ieltszone.zonelifeservice.entity.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponseWithSize {
    private long size;
    private List<RewardResponse> rewardResponses;
}