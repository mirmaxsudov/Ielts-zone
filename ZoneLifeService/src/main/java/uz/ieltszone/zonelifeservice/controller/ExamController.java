package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.aop.annotations.ExistsUserById;
import uz.ieltszone.zonelifeservice.entity.request.ExamRequest;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.ExamService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/exam")
public class ExamController {
    private final ExamService examService;

    @PostMapping("/save/{teacherId}")
    public ApiResponse<?> save(@PathVariable("teacherId") @ExistsUserById Long teacherId, @RequestBody ExamRequest examRequest) {
        return examService.save(teacherId, examRequest);
    }

    @DeleteMapping("/delete/{teacherId}")
    private ApiResponse<?> delete(@PathVariable("teacherId") @ExistsUserById Long teacherId, @RequestParam("examId") Long examId) {
        return examService.delete(teacherId, examId);
    }
}