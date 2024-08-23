package uz.ieltszone.zonelifeservice.service.base;

import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

public interface ExamService {
    ApiResponse<?> save(Long teacherId, MultipartFile file);
}
