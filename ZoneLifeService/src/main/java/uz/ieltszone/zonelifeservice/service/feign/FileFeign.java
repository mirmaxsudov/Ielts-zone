package uz.ieltszone.zonelifeservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.zonelifeservice.entity.response.ExamResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;

import java.util.List;

@FeignClient(name = "IELTS-ZONE-FILE-SERVICE")
public interface FileFeign {
    @PostMapping(value = "/uploads", consumes = "multipart/form-data", produces = "application/json")
    ResponseEntity<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files);

    @PostMapping("/upload")
    ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file);

    @DeleteMapping("/delete/{attachmentId}")
    ApiResponse<?> deleteAttachment(@PathVariable("attachmentId") Long attachmentId);

    @PostMapping(value = "/save-excel-and-return-values", consumes = "multipart/form-data", produces = "application/json")
    ApiResponse<ExamResponse> uploadExcel(@RequestParam("excel") MultipartFile file);
}