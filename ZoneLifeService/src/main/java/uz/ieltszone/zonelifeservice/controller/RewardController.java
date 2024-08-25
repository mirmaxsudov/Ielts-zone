package uz.ieltszone.zonelifeservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.request.RewardRequest;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponse;
import uz.ieltszone.zonelifeservice.entity.response.RewardResponseWithSize;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.RewardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/zone-life/reward")
public class RewardController {
    private final RewardService rewardService;

    @PostMapping("/save")
    public ApiResponse<?> save(@RequestBody RewardRequest rewardRequest) {
        return rewardService.save(rewardRequest);
    }

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
}