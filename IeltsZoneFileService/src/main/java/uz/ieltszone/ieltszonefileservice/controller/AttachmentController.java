package uz.ieltszone.ieltszonefileservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.ieltszonefileservice.aop.CheckRole;
import uz.ieltszone.ieltszonefileservice.entity.response.ExamResponse;
import uz.ieltszone.ieltszonefileservice.payload.ApiResponse;
import uz.ieltszone.ieltszonefileservice.service.base.AttachmentService;

import java.util.List;

@RestController
@Valid
@RequiredArgsConstructor
@RequestMapping("/api/v1/attachment")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @GetMapping("/get/{attachmentId}")
    public ResponseEntity<FileUrlResource> getPhoto(@PathVariable("attachmentId") Long attachmentId) {
        System.out.println("attachmentId = " + attachmentId);
        return attachmentService.getPhoto(attachmentId);
    }

    @CheckRole(roles = {"TEACHER", "ADMIN"})
    @PostMapping(value = "/uploads", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(attachmentService.uploadFiles(files));
    }

    @PostMapping("/upload")
    @CheckRole(roles = {"TEACHER", "ADMIN"})
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadFile(file));
    }

    @CheckRole(roles = {"ADMIN"})
    @PostMapping("/save-excel-and-return-values")
    public ApiResponse<ExamResponse> saveExcelAndReturnValues(@RequestParam("excel") MultipartFile file) {
        return attachmentService.saveExcelAndReturnValues(file);
    }

    @CheckRole(roles = {"ADMIN", "TEACHER"})
    @DeleteMapping("/delete/{attachmentId}")
    public ApiResponse<?> deleteById(@PathVariable Long attachmentId) {
        return attachmentService.deleteById(attachmentId);
    }

    @CheckRole(roles = {"ADMIN", "TEACHER"})
    @GetMapping("/exists/{attachmentId}")
    public ApiResponse<Boolean> existsAttachment(@PathVariable("attachmentId") Long attachmentId) {
        return attachmentService.existsAttachment(attachmentId);
    }
}