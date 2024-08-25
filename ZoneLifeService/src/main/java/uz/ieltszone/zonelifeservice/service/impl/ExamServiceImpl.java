package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.request.ExamRequest;
import uz.ieltszone.zonelifeservice.entity.request.ExamRequestInner;
import uz.ieltszone.zonelifeservice.entity.request.ResultRequest;
import uz.ieltszone.zonelifeservice.entity.response.ResultResponse;
import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.entity.response.teacher_exam.TeachersExamsResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.ExamRepository;
import uz.ieltszone.zonelifeservice.service.base.ExamService;
import uz.ieltszone.zonelifeservice.service.base.ResultService;
import uz.ieltszone.zonelifeservice.service.feign.FileFeign;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;
import uz.ieltszone.zonelifeservice.service.mapper.ExamMapper;
import uz.ieltszone.zonelifeservice.service.mapper.ResultMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final ResultService resultService;
    private final FileFeign fileFeign;
    private final UserFeign userFeign;
    private final ExamMapper examMapper;
    private final ResultMapper resultMapper;

    @Override
    @Modifying
    @Transactional
    public ApiResponse<?> save(Long teacherId, ExamRequest examRequest) {
        Exam exam = new Exam();

        exam.setTeacherId(teacherId);
        exam.setAddedAt(LocalDate.now());
        exam.setExamType(examRequest.getExamType());
        exam.setExamLevel(examRequest.getExamLevel());
        exam.setExamDate(examRequest.getExamDate());

        ExamRequestInner examRequestInner = examRequest.getExamRequestInner();

        exam.setExcelFileId(examRequestInner.getExcelFileId());

        examRepository.save(exam);

        List<ResultRequest> resultResponses = examRequestInner.getResultResponses();

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

    @Override
    public ApiResponse<TeachersExamsResponse> getExamsByTeacherId(Long teacherId) {
        List<Exam> exams = examRepository.findAllByTeacherId(teacherId);

        if (exams == null || exams.isEmpty())
            return new ApiResponse<TeachersExamsResponse>()
                    .success("No data found", null);

        TeachersExamsResponse teachersExamsResponse = new TeachersExamsResponse();

        TeacherResponse teacherResponse = userFeign.getById(teacherId);
        teachersExamsResponse.setTeacherResponse(teacherResponse);
        teachersExamsResponse.setExamTeacherResponses(
                exams.stream()
                        .map(examMapper::toResponse)
                        .toList()
        );

        return new ApiResponse<TeachersExamsResponse>()
                .success("Successfully found", teachersExamsResponse);
    }

    @Override
    public ApiResponse<List<ResultResponse>> getResultsByExamId(Long examId) {
        List<Result> results = resultService.getResultsByExamId(examId);

        if (results == null || results.isEmpty())
            return new ApiResponse<List<ResultResponse>>()
                    .success("No data found", null);

        return new ApiResponse<List<ResultResponse>>()
                .success(
                        "Successfully found",
                        results.stream()
                                .map(resultMapper::toResponse)
                                .toList()
                );
    }

    static class TotalSums {
        private Float listeningTotal;
        private Float readingTotal;
        private Float speakingTotal;
        private Float writingTotal;
    }
}