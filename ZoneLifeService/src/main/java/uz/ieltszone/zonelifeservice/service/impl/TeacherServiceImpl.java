package uz.ieltszone.zonelifeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.TeacherService;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final UserFeign userFeign;

    @Override
    public ApiResponse<TeacherResponse> getById(Long teacherId) {
        return null;
    }
}