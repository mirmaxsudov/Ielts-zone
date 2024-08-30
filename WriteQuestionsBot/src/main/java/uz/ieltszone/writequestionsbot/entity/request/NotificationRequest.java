package uz.ieltszone.writequestionsbot.entity.request;

import lombok.*;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long chatId;
    private String body;
    private UserRole willSendTo;
    private Attachment attachment;
}