package uz.ieltszone.writequestionsbot.entity.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private String questionAsText;
    private String answerAsText;
    private String whenTime;
    private List<Long> attachments;
}