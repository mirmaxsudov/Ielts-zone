package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface TeacherService {
    ApiResponse<TeacherResponse> getById(Long teacherId);

    ApiResponse<Long> getRatingForTeacherAndAllTheTime(Long teacherId);
}
