package uz.ieltszone.ieltszoneuserservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.AttachmentResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;

@FeignClient(name = "IELTS-ZONE-FILE-SERVICE")
public interface AttachmentFeign {
    @GetMapping("/api/v1/attachment/{attachmentId}")
    AttachmentResponse getAttachment(@PathVariable("attachmentId") Long attachmentId);

    @DeleteMapping("/delete/{attachmentId}")
    ApiResponse<?> deleteAttachment(@PathVariable("attachmentId") Long attachmentId);
}