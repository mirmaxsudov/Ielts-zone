package uz.ieltszone.zonelifeservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.EnumUtils;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.response.teacher_exam.ExamTeacherResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExamMapper {
    public ExamTeacherResponse toResponse(Exam exam) {
        if (exam == null)
            return null;

        List<Result> results = exam.getResults();
        return ExamTeacherResponse.builder()
                .examId(exam.getId())
                .examDate(exam.getExamDate())
                .level(exam.getExamLevel())
                .type(exam.getExamType())
                .excelFileId(exam.getExcelFileId())
                .addedAt(exam.getAddedAt())
                .sizeOfResults(results == null ? 0 : results.size())
                .writingTotal(exam.getWriting())
                .readingTotal(exam.getReading())
                .speakingTotal(exam.getSpeaking())
                .listeningTotal(exam.getListening())
                .totalAvg(
                        (exam.getListening() + exam.getSpeaking() + exam.getReading() + exam.getWriting()) / 4
                )
                .build();
    }
}