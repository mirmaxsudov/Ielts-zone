package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.aop.annotations.ExistsUserById;
import uz.ieltszone.zonelifeservice.entity.dto.request.ExamRequest;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.teacher_exam.TeachersExamsResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.ExamService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/exam")
public class ExamController {
    private final ExamService examService;

    @PostMapping("/save/{teacherId}")
    public ApiResponse<?> save(@PathVariable("teacherId") @ExistsUserById Long teacherId, @RequestBody ExamRequest examRequest) {
        return examService.save(teacherId, examRequest);
    }

    @GetMapping("/get-exams-by-teacher-id/{teacherId}")
    public ApiResponse<TeachersExamsResponse> getExamsByTeacherId(@PathVariable("teacherId") @ExistsUserById Long teacherId) {
        return examService.getExamsByTeacherId(teacherId);
    }

    @GetMapping("/get-results-by-exam-id/{examId}")
    public ApiResponse<List<ResultResponse>> getResultsByExamId(@PathVariable("examId") Long examId) {
        return examService.getResultsByExamId(examId);
    }

    @DeleteMapping("/delete/{teacherId}")
    public ApiResponse<?> delete(@PathVariable("teacherId") @ExistsUserById Long teacherId, @RequestParam("examId") Long examId) {
        return examService.delete(teacherId, examId);
    }
}