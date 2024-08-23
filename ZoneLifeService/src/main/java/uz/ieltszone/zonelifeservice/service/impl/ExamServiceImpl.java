package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
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

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final ResultService resultService;
    private final FileFeign fileFeign;

    @Override
    @Modifying
    @Transactional
    public ApiResponse<?> save(Long teacherId, MultipartFile file) {

        Exam exam = new Exam();

        exam.setAddedAt(LocalDate.now());
        exam.setTeacherId(teacherId);

        ApiResponse<ExamResponse> apiResponse = fileFeign.uploadExcel(file);
        ExamResponse examResponse = apiResponse.getData();

        List<ResultResponse> resultResponses = examResponse.getResultResponses();

        examRepository.save(exam);

        CompletableFuture.runAsync(
                () -> resultService.save(resultResponses, exam)
        );

        TotalSums totals = resultResponses.stream()
                .reduce(new TotalSums(), (acc, resultResponse) -> {
                    acc.listeningTotal += resultResponse.getListening() != null ? resultResponse.getListening() : 0.0f;
                    acc.readingTotal += resultResponse.getReading() != null ? resultResponse.getReading() : 0.0f;
                    acc.speakingTotal += resultResponse.getSpeaking() != null ? resultResponse.getSpeaking() : 0.0f;
                    acc.writingTotal += resultResponse.getWriting() != null ? resultResponse.getWriting() : 0.0f;
                    return acc;
                }, (a, b) -> {
                    a.listeningTotal += b.listeningTotal;
                    a.readingTotal += b.readingTotal;
                    a.speakingTotal += b.speakingTotal;
                    a.writingTotal += b.writingTotal;
                    return a;
                });

        exam.setListening(totals.listeningTotal);
        exam.setReading(totals.readingTotal);
        exam.setSpeaking(totals.speakingTotal);
        exam.setWriting(totals.writingTotal);

        examRepository.save(exam);

        return new ApiResponse<>().success("Successfully saved");
    }

    @Override
    @Transactional
    public ApiResponse<?> delete(Long teacherId, Long examId) {
        resultService.deleteByExamId(examId);
        examRepository.deleteById(examId);
        return new ApiResponse<>().success("Successfully deleted");
    }

    static class TotalSums {
        private Float listeningTotal;
        private Float readingTotal;
        private Float speakingTotal;
        private Float writingTotal;
    }
}