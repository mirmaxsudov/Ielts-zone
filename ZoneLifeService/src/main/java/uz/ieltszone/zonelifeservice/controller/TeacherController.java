package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.TeacherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping("/get-by-id/{teacherId}")
    public ApiResponse<TeacherResponse> getById(@PathVariable("teacherId") Long teacherId) {
        return teacherService.getById(teacherId);
    }

    @GetMapping("/get-rating-for-teacher/{teacherId}")
    public ApiResponse<Long> getRatingForTeacherAndAllTheTime(@PathVariable("teacherId") Long teacherId) {
        return teacherService.getRatingForTeacherAndAllTheTime(teacherId);
    }
}