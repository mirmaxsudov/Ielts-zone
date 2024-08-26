package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.aop.CheckRole;
import uz.ieltszone.zonelifeservice.aop.CurrentUser;
import uz.ieltszone.zonelifeservice.config.security.UserDetailsDTO;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.dto.request.RewardRequestUpdate;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.RewardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/reward")
public class RewardController {
    private final RewardService rewardService;

    @GetMapping("/page")
    @CheckRole(roles = "ADMIN")
    public ResponseEntity<ApiResponse<RewardResponseWithSize>> getAllByPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.ok(rewardService.getAllByPage(page, size));
    }

    @GetMapping("/all")
    @CheckRole(roles = "ADMIN")
    public ApiResponse<RewardResponseWithSize> getAll() {
        return rewardService.getAll();
    }

    @GetMapping("/get")
    @CheckRole(roles = "TEACHER")
    public ApiResponse<RewardResponse> getById(@CurrentUser UserDetailsDTO userDetailsDTO) {
        return rewardService.getById(userDetailsDTO.getId());
    }

    @GetMapping("/size")
    @CheckRole(roles = "ADMIN")
    public ApiResponse<Long> getSize() {
        return rewardService.getSize();
    }

    @CheckRole(roles = "ADMIN")
    @GetMapping("/get-all-by-teacher/{teacherId}")
    public RewardResponseWithSize getAllByTeacher(@PathVariable Long teacherId) {
        return rewardService.getAllByTeacher(teacherId);
    }

    @PostMapping("/save")
    @CheckRole(roles = "ADMIN")
    public ResponseEntity<ApiResponse<?>> save(@RequestBody RewardRequest rewardRequest) {
        return ResponseEntity.ok(rewardService.save(rewardRequest));
    }

    @CheckRole(roles = {"ADMIN", "MANAGER"})
    @PostMapping("/set-reward/{teacherId}")
    public ResponseEntity<ApiResponse<?>> setReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.setReward(teacherId, rewardId);
    }

    @CheckRole(roles = "ADMIN")
    @PutMapping("/update/{rewardId}")
    public ApiResponse<?> update(@PathVariable("rewardId") Long rewardId, @RequestBody RewardRequestUpdate updateRequest) {
        return rewardService.update(rewardId, updateRequest);
    }

    @CheckRole(roles = "ADMIN")
    @DeleteMapping("/delete-reward-from-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<?>> deleteReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.deleteRewardFromTeacher(teacherId, rewardId);
    }

    @CheckRole(roles = "ADMIN")
    @DeleteMapping("/delete/{rewardId}")
    public void delete(@PathVariable("rewardId") Long rewardId) {
        rewardService.delete(rewardId);
    }
}