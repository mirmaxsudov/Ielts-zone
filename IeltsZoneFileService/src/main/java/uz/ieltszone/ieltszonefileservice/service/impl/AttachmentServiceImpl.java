package uz.ieltszone.ieltszonefileservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ieltszone.ieltszonefileservice.entity.Attachment;
import uz.ieltszone.ieltszonefileservice.exceptions.CustomNotFoundException;
import uz.ieltszone.ieltszonefileservice.payload.ApiResponse;
import uz.ieltszone.ieltszonefileservice.repository.AttachmentRepository;
import uz.ieltszone.ieltszonefileservice.service.base.AttachmentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Value("${file.upload-dir}")
    private String BASE_URL;

    @Override
    @Modifying
    public List<Long> uploadFiles(List<MultipartFile> files) {
        List<Long> ids = new ArrayList<>();

        for (MultipartFile file : files)
            ids.add(uploadFile(file));

        return ids;
    }

    @Override
    @Modifying
    @SneakyThrows
    public Long uploadFile(MultipartFile file) {
        Attachment attachment = saveToDataBase(file);

        String fileURL = BASE_URL +
                "/" + System.currentTimeMillis() +
                "_" + UUID.randomUUID() +
                "_" + attachment.getFileName() +
                "." + attachment.getExtension();

        attachment.setFileUrl(fileURL);
        storeFile(file, fileURL);
        attachmentRepository.save(attachment);

        return attachment.getId();
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteById(Long attachmentId) {
        Attachment attachment = getById(attachmentId);

        deleteFromStorage(attachment.getFileUrl());
        attachmentRepository.deleteById(attachmentId);

        return new ApiResponse<>().success();
    }

    private void deleteFromStorage(String fileUrl) {
        try {
            Files.delete(Paths.get(fileUrl));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public Attachment getById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(
                        () -> new CustomNotFoundException("Attachment not found")
                );
    }

    private void storeFile(MultipartFile file, String fileURL) {
        try {
            Files.write(Paths.get(fileURL), file.getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Attachment saveToDataBase(MultipartFile file) {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setExtension(getExtension(Objects.requireNonNull(file.getOriginalFilename())));

        return attachmentRepository.save(attachment);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}