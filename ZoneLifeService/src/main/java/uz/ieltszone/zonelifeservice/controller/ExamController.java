package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.aop.annotations.ExistsUserById;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.ExamService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/exam")
public class ExamController {
    private final ExamService examService;

    @PostMapping("/save/{teacherId}")
    public ApiResponse<?> save(@PathVariable("teacherId") @ExistsUserById Long teacherId, @RequestPart("excel") MultipartFile file) {
        return examService.save(teacherId, file);
    }
}