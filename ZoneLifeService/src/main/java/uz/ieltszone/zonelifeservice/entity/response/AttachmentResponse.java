package uz.ieltszone.zonelifeservice.entity.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttachmentResponse {
    private Long attachmentId;
    private String url;
}