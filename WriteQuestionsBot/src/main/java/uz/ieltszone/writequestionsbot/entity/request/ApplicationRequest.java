package uz.ieltszone.writequestionsbot.entity.request;

import lombok.*;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private LearningCenter center;
    private boolean task1Doing;
    private Task task1;
    private String examDate;
    private String task1Question;
    private Boolean isPhotosExistForTask1;
    private List<Attachment> attachmentsUrlsForTask1;
    private boolean task2Doing;
    private Task task2;
    private String task2Question;
    private Long studentId;
    private Long chatId;
    private Instant createdAt;
}