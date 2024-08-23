package uz.ieltszone.zonelifeservice.entity.request;

import lombok.Getter;
import uz.ieltszone.zonelifeservice.entity.enums.ExamLevel;
import uz.ieltszone.zonelifeservice.entity.enums.ExamType;

@Getter
public class ExamRequest {
    private ExamType examType;
    private ExamLevel examLevel;
    private ExamRequestInner examRequestInner;
}