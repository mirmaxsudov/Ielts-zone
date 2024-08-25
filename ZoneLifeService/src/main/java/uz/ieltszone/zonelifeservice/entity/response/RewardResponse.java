package uz.ieltszone.zonelifeservice.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    private Long id;
    private String rewardName;
    private String description;
    private Long createdById;
    private LocalDate createdAt;
    private AttachmentResponse attachmentResponse;
}