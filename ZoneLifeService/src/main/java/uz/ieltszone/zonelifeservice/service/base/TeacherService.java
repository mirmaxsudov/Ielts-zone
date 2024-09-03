package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.dto.response.MonthsTeacherResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.impl.TeacherServiceImpl;

public interface TeacherService {
    ApiResponse<TeacherResponse> getById(Long teacherId);

    ApiResponse<Long> getRatingForTeacherAndAllTheTime(Long teacherId);

    ApiResponse<MonthsTeacherResponse> getMonthOfTeachersForIELTS();
}