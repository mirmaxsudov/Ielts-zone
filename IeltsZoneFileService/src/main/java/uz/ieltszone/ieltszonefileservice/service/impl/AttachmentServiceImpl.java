package uz.ieltszone.ieltszonefileservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.UriEncoder;
import uz.ieltszone.ieltszonefileservice.entity.Attachment;
import uz.ieltszone.ieltszonefileservice.entity.response.ExamResponse;
import uz.ieltszone.ieltszonefileservice.entity.response.ResultResponse;
import uz.ieltszone.ieltszonefileservice.exceptions.CustomNotFoundException;
import uz.ieltszone.ieltszonefileservice.payload.ApiResponse;
import uz.ieltszone.ieltszonefileservice.repository.AttachmentRepository;
import uz.ieltszone.ieltszonefileservice.service.base.AttachmentService;

import java.io.IOException;
import java.io.InputStream;
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
                "\\" + System.currentTimeMillis() +
                "_" + UUID.randomUUID() +
                "_" + attachment.getFileName();

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

    @Override
    public ResponseEntity<FileUrlResource> getPhoto(Long attachmentId) {
        Attachment attachment = getById(attachmentId);

        try {
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=" +
                                    UriEncoder.encode(attachment.getFileName())
                    ).contentType(MediaType.parseMediaType(attachment.getFileType()))
                    .body(
                            new FileUrlResource(
                                    attachment.getFileUrl()
                            )
                    );
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ApiResponse<ExamResponse> saveExcelAndReturnValues(MultipartFile file) {
        Attachment attachment = saveToDataBase(file);
        storeFile(file, attachment.getFileUrl());

        ExamResponse examResponse = new ExamResponse();
        examResponse.setExcelFileId(attachment.getId());

        List<ResultResponse> responses = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Float> valueMap = new HashMap<>();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim().toLowerCase();
                        switch (cellValue) {
                            case "writing" -> {
                                Cell nextCell = row.getCell(cell.getColumnIndex() + 1);
                                valueMap.put("writing", getCellValueAsFloat(nextCell));
                            }
                            case "reading" -> {
                                Cell nextCell = row.getCell(cell.getColumnIndex() + 1);
                                valueMap.put("reading", getCellValueAsFloat(nextCell));
                            }
                            case "listening" -> {
                                Cell nextCell = row.getCell(cell.getColumnIndex() + 1);
                                valueMap.put("listening", getCellValueAsFloat(nextCell));
                            }
                            case "speaking" -> {
                                Cell nextCell = row.getCell(cell.getColumnIndex() + 1);
                                valueMap.put("speaking", getCellValueAsFloat(nextCell));
                            }
                        }
                    }
                }
            }

            ResultResponse resultResponse = new ResultResponse();

            resultResponse.setWriting(valueMap.get("writing"));
            resultResponse.setReading(valueMap.get("reading"));
            resultResponse.setListening(valueMap.get("listening"));
            resultResponse.setSpeaking(valueMap.get("speaking"));

            responses.add(resultResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        examResponse.setResultResponses(responses);
        return new ApiResponse<ExamResponse>()
                .success("Success", examResponse);
    }

    private Float getCellValueAsFloat(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (float) cell.getNumericCellValue();
            case STRING:
                try {
                    return Float.parseFloat(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
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