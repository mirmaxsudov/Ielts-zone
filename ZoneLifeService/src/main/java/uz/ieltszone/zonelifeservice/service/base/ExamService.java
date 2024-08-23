package uz.ieltszone.zonelifeservice.service.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.entity.request.ExamRequest;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface ExamService {
    ApiResponse<?> save(Long teacherId, ExamRequest examRequest);

    ApiResponse<?> delete(Long teacherId, Long examId);
}
