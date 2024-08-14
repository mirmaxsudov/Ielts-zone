package uz.ieltszone.writequestionsbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.writequestionsbot.entity.LearningCenter;
import uz.ieltszone.writequestionsbot.repository.LearningCenterRepository;
import uz.ieltszone.writequestionsbot.service.base.LearningCenterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningCenterServiceImpl implements LearningCenterService {
    private final LearningCenterRepository learningCenterRepository;

    @Override
    public List<LearningCenter> getAll() {
        return learningCenterRepository.findAll();
    }
}