package uz.ieltszone.zonelifeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.repository.RewardRepository;
import uz.ieltszone.zonelifeservice.repository.RewardTeachersRepository;
import uz.ieltszone.zonelifeservice.service.base.RewardService;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;
    private final RewardTeachersRepository rewardTeachersRepository;
}