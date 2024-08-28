package uz.ieltszone.zonelifeservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ApiResponse<RewardResponseWithSize>> getAllByPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.ok(rewardService.getAllByPage(page, size));
    }

    @GetMapping("/all")
    public ApiResponse<RewardResponseWithSize> getAll() {
        return rewardService.getAll();
    }

    @GetMapping("/get")
    public ApiResponse<RewardResponse> getById(UserDetailsDTO userDetailsDTO) {
        return rewardService.getById(userDetailsDTO.getId());
    }

    @GetMapping("/size")
    public ApiResponse<Long> getSize() {
        return rewardService.getSize();
    }

    @GetMapping("/get-all-by-teacher/{teacherId}")
    public RewardResponseWithSize getAllByTeacher(@PathVariable Long teacherId) {
        return rewardService.getAllByTeacher(teacherId);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> save(@RequestBody RewardRequest rewardRequest) {
        return ResponseEntity.ok(rewardService.save(rewardRequest));
    }

    @PostMapping("/set-reward/{teacherId}")
    public ResponseEntity<ApiResponse<?>> setReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.setReward(teacherId, rewardId);
    }

    @PutMapping("/update/{rewardId}")
    public ApiResponse<?> update(@PathVariable("rewardId") Long rewardId, @RequestBody RewardRequestUpdate updateRequest) {
        return rewardService.update(rewardId, updateRequest);
    }

    @DeleteMapping("/delete-reward-from-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<?>> deleteReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.deleteRewardFromTeacher(teacherId, rewardId);
    }

    @DeleteMapping("/delete/{rewardId}")
    public void delete(@PathVariable("rewardId") Long rewardId) {
        rewardService.delete(rewardId);
    }
}