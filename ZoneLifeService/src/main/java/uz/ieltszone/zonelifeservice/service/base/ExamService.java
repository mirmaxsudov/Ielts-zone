package uz.ieltszone.zonelifeservice.service.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface ExamService {
    ApiResponse<?> save(Long teacherId, MultipartFile file);
    ApiResponse<?> delete(Long teacherId, Long examId);
}
