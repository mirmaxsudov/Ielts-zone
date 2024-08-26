package uz.ieltszone.zonelifeservice.entity.dto.response.teacher_exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import uz.ieltszone.zonelifeservice.entity.enums.ExamLevel;
import uz.ieltszone.zonelifeservice.entity.enums.ExamType;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamTeacherResponse {
    private Long examId;
    private Float writingTotal;
    private Float readingTotal;
    private Float speakingTotal;
    private Float listeningTotal;
    private Float totalAvg;
    private LocalDate examDate;
    private LocalDate addedAt;
    private Long excelFileId;
    private Integer sizeOfResults;
    private ExamLevel level;
    private ExamType type;
}