package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.request.ExamRequest;
import uz.ieltszone.zonelifeservice.entity.response.ResultResponse;
import uz.ieltszone.zonelifeservice.entity.response.teacher_exam.TeachersExamsResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

import java.util.List;

public interface ExamService {
    ApiResponse<?> save(Long teacherId, ExamRequest examRequest);

    ApiResponse<?> delete(Long teacherId, Long examId);

    ApiResponse<TeachersExamsResponse> getExamsByTeacherId(Long teacherId);

    ApiResponse<List<ResultResponse>> getResultsByExamId(Long examId);
}
