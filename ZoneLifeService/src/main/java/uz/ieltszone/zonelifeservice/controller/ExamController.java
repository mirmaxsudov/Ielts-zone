package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.aop.CheckRole;
import uz.ieltszone.zonelifeservice.aop.CurrentUser;
import uz.ieltszone.zonelifeservice.config.security.UserDetailsDTO;
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

    @PostMapping("/save")
    @CheckRole(roles = "ADMIN")
    public ApiResponse<?> save(@RequestBody ExamRequest examRequest, @CurrentUser UserDetailsDTO userDetailsDTO) {
        return examService.save(userDetailsDTO.getId(), examRequest);
    }

    @CheckRole(roles = {"ADMIN", "TEACHER"})
    @GetMapping("/get-exams-by-teacher-id")
    public ApiResponse<TeachersExamsResponse> getExamsByTeacherId(@CurrentUser UserDetailsDTO userDetailsDTO) {
        return examService.getExamsByTeacherId(userDetailsDTO.getId());
    }

    @CheckRole(roles = {"ADMIN", "TEACHER"})
    @GetMapping("/get-results-by-exam-id/{examId}")
    public ApiResponse<List<ResultResponse>> getResultsByExamId(@PathVariable("examId") Long examId) {
        return examService.getResultsByExamId(examId);
    }

    @CheckRole(roles = "ADMIN")
    @DeleteMapping("/delete")
    public ApiResponse<?> delete(@RequestParam("examId") Long examId, @CurrentUser UserDetailsDTO userDetailsDTO) {
        return examService.delete(userDetailsDTO.getId(), examId);
    }
}