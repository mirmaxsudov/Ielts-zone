package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.dto.request.ExamRequest;
import uz.ieltszone.zonelifeservice.entity.dto.request.ExamRequestInner;
import uz.ieltszone.zonelifeservice.entity.dto.request.RateRequest;
import uz.ieltszone.zonelifeservice.entity.dto.request.ResultRequest;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.teacher_exam.TeachersExamsResponse;
import uz.ieltszone.zonelifeservice.entity.payload.TotalSums;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.ExamRepository;
import uz.ieltszone.zonelifeservice.service.base.ExamService;
import uz.ieltszone.zonelifeservice.service.base.RateService;
import uz.ieltszone.zonelifeservice.service.base.ResultService;
import uz.ieltszone.zonelifeservice.service.feign.FileFeign;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;
import uz.ieltszone.zonelifeservice.service.mapper.ExamMapper;
import uz.ieltszone.zonelifeservice.service.mapper.ResultMapper;

import java.time.LocalDate;
import java.time.Month;
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
    private final RateService rateService;

    @Override
    @Modifying
    @Transactional
    public ApiResponse<?> save(Long teacherId, ExamRequest examRequest) {
//        initialize exam
        Exam exam = new Exam();
        exam.setAddedAt(LocalDate.now());
        exam.setExamDate(examRequest.getExamDate());
        exam.setExamType(examRequest.getExamType());
        exam.setPassMark(examRequest.getPassMark());
        exam.setExamLevel(examRequest.getExamLevel());

        ExamRequestInner examRequestInner = examRequest.getExamRequestInner();
        Long excelFileId = examRequestInner.getExcelFileId();
        List<ResultRequest> resultRequests = examRequestInner.getResultResponses();

        exam.setExcelFileId(excelFileId);

//        save exam
        examRepository.save(exam);

//        save result
        resultService.save(resultRequests, exam);

//        save exam again with avg values

        TotalSums totalSums = new TotalSums();

        float listeningTotal = 0f;
        float readingTotal = 0f;
        float speakingTotal = 0f;
        float writingTotal = 0f;

        for (ResultRequest resultRequest : resultRequests) {
            listeningTotal += resultRequest.getListening();
            readingTotal += resultRequest.getReading();
            speakingTotal += resultRequest.getSpeaking();
            writingTotal += resultRequest.getWriting();
        }

        totalSums.setListeningTotal(listeningTotal);
        totalSums.setReadingTotal(readingTotal);
        totalSums.setSpeakingTotal(speakingTotal);
        totalSums.setWritingTotal(writingTotal);

        exam.setReading(totalSums.getReadingTotal() / resultRequests.size());
        exam.setSpeaking(totalSums.getSpeakingTotal() / resultRequests.size());
        exam.setWriting(totalSums.getWritingTotal() / resultRequests.size());
        exam.setListening(totalSums.getListeningTotal() / resultRequests.size());

        exam.setTotal(
                (exam.getListening() + exam.getReading() + exam.getSpeaking() + exam.getWriting()) / 4
        );

        examRepository.save(exam);

//      get monthly rate for exam based month

        Rate rate = rateService.getByTeacherIdAndMonth(teacherId, getMonth(exam.getExamDate()));

//      if there is no monthly rating for current month, save it
        if (rate == null) {
            RateRequest rateRequest = new RateRequest();
            rateRequest.setTeacherId(teacherId);
            rateRequest.setAvg(exam.getTotal());
            rateRequest.setDate(LocalDate.now());
            rateRequest.setMonth(getMonth(exam.getExamDate()));

            rateService.save(rateRequest);

            rate = rateService.getByTeacherIdAndMonth(teacherId, getMonth(exam.getExamDate()));
        }

        exam.setRate(rate);
        examRepository.save(exam);

        return new ApiResponse<>().success("Successfully saved");
    }

    private Month getMonth(LocalDate date) {
        return Month.of(date.getMonthValue());
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
}