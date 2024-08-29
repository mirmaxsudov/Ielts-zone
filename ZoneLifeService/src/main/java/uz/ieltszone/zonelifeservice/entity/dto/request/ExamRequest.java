package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.Getter;
import lombok.ToString;
import uz.ieltszone.zonelifeservice.entity.enums.ExamLevel;
import uz.ieltszone.zonelifeservice.entity.enums.ExamType;

import java.time.LocalDate;

@Getter
@ToString
public class ExamRequest {
    private ExamType examType;
    private ExamLevel examLevel;
    private LocalDate examDate;
    private Float passMark;
    private Long teacherId;
    private ExamRequestInner examRequestInner;
}