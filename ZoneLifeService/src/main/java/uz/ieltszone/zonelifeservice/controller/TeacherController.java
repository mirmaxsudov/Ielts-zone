package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.TeacherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping("/{teacherId}")
    public ApiResponse<TeacherResponse> getById(@PathVariable("teacherId") Long teacherId) {
        return teacherService.getById(teacherId);
    }
}