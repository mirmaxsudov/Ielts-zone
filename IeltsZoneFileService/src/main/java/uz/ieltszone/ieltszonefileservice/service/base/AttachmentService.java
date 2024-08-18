package uz.ieltszone.ieltszonefileservice.service.base;

import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.ieltszonefileservice.entity.Attachment;
import uz.ieltszone.ieltszonefileservice.payload.ApiResponse;

import java.util.List;

public interface AttachmentService {
    List<Long> uploadFiles(List<MultipartFile> files);

    Long uploadFile(MultipartFile file);

    ApiResponse<?> deleteById(Long attachmentId);

    Attachment getById(Long attachmentId);
}