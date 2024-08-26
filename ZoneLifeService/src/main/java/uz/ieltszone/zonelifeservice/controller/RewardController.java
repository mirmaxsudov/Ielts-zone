package uz.ieltszone.zonelifeservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/all")
    public ApiResponse<RewardResponseWithSize> getAll() {
        return rewardService.getAll();
    }

    @GetMapping("/get/{id}")
    public ApiResponse<RewardResponse> getById(@PathVariable Long id) {
        return rewardService.getById(id);
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
    public ApiResponse<?> save(@RequestBody RewardRequest rewardRequest) {
        return rewardService.save(rewardRequest);
    }

    @PutMapping("/update/{rewardId}")
    public ApiResponse<?> update(@PathVariable("rewardId") Long rewardId, @RequestBody RewardRequestUpdate updateRequest) {
        return rewardService.update(rewardId, updateRequest);
    }

    @GetMapping("/set-reward/{teacherId}")
    public ResponseEntity<ApiResponse<?>> setReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.setReward(teacherId, rewardId);
    }

    @DeleteMapping("/delete-reward-from-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<?>> deleteReward(@PathVariable("teacherId") Long teacherId, @RequestParam("rewardId") Long rewardId) {
        return rewardService.deleteRewardFromTeacher(teacherId, rewardId);
    }
}