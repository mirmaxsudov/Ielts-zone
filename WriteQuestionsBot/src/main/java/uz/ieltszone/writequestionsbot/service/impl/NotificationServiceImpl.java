package uz.ieltszone.writequestionsbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.writequestionsbot.repository.NotificationRepository;
import uz.ieltszone.writequestionsbot.service.base.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
}