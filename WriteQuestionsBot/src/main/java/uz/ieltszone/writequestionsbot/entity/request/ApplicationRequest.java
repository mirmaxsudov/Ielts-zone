package uz.ieltszone.writequestionsbot.entity.request;

import lombok.*;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private LearningCenter learningCenter;
    private Task task;
    private String questionAsText;
    private String answerAsText;
    private String whenTime;
    private boolean isExistPhoto;
    private Long studentChatId;
    private List<Long> attachments = new LinkedList<>();
}