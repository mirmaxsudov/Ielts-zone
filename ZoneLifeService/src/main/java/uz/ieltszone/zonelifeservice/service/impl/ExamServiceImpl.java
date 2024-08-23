package uz.ieltszone.zonelifeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.response.ExamResponse;
import uz.ieltszone.zonelifeservice.entity.response.ResultResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.ExamRepository;
import uz.ieltszone.zonelifeservice.service.base.ExamService;
import uz.ieltszone.zonelifeservice.service.base.ResultService;
import uz.ieltszone.zonelifeservice.service.feign.FileFeign;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final ResultService resultService;
    private final FileFeign fileFeign;

    @Override
    public ApiResponse<?> save(Long teacherId, MultipartFile file) {
        Exam exam = new Exam();

        exam.setAddedAt(LocalDateTime.now());
        exam.setTeacherId(teacherId);

        ApiResponse<ExamResponse> apiResponse = fileFeign.uploadExcel(file);
        ExamResponse examResponse = apiResponse.getData();

        exam.setExcelFileId(examResponse.getExcelFileId());

        List<ResultResponse> resultResponses = examResponse.getResultResponses();

        examRepository.save(exam);
        resultService.save(resultResponses, exam);

        TotalSums totals = resultResponses.stream()
                .reduce(new TotalSums(), (acc, resultResponse) -> {
                    acc.listeningTotal += resultResponse.getListening() != null ? resultResponse.getListening() : 0.0;
                    acc.readingTotal += resultResponse.getReading() != null ? resultResponse.getReading() : 0.0;
                    acc.speakingTotal += resultResponse.getSpeaking() != null ? resultResponse.getSpeaking() : 0.0;
                    acc.writingTotal += resultResponse.getWriting() != null ? resultResponse.getWriting() : 0.0;
                    return acc;
                }, (a, b) -> {
                    a.listeningTotal += b.listeningTotal;
                    a.readingTotal += b.readingTotal;
                    a.speakingTotal += b.speakingTotal;
                    a.writingTotal += b.writingTotal;
                    return a;
                });

        exam.setListeningTotalBall(totals.listeningTotal);
        exam.setReadingTotalBall(totals.readingTotal);
        exam.setSpeakingTotalBall(totals.speakingTotal);
        exam.setWritingTotalBall(totals.writingTotal);

        examRepository.save(exam);

        return new ApiResponse<>().success("Successfully saved");
    }

    static class TotalSums {
        private Double listeningTotal;
        private Double readingTotal;
        private Double speakingTotal;
        private Double writingTotal;
    }
}