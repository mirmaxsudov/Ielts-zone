package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ieltszone.zonelifeservice.aop.CheckRole;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.ResultService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/result")
public class ResultController {
    private final ResultService resultService;

    @CheckRole(roles = "TEACHER")
    @GetMapping("/get-all-by-exam-id/{examId}")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getAllByExamId(@PathVariable("examId") Long examId) {
        return ResponseEntity.ok(resultService.getAllByExamId(examId));
    }
}