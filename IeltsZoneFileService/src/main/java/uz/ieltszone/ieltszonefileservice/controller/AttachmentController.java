package uz.ieltszone.ieltszonefileservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.ieltszonefileservice.payload.ApiResponse;
import uz.ieltszone.ieltszonefileservice.service.base.AttachmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attachment")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/uploads")
    public ResponseEntity<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(attachmentService.uploadFiles(files));
    }

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadFile(file));
    }

    @DeleteMapping("/delete/{attachmentId}")
    public ApiResponse<?> deleteById(@PathVariable Long attachmentId) {
        return attachmentService.deleteById(attachmentId);
    }
}