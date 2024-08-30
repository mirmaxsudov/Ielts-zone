package uz.ieltszone.writequestionsbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.writequestionsbot.entity.Notification;
import uz.ieltszone.writequestionsbot.entity.request.NotificationRequest;
import uz.ieltszone.writequestionsbot.repository.NotificationRepository;
import uz.ieltszone.writequestionsbot.service.base.NotificationService;
import uz.ieltszone.writequestionsbot.service.base.UserService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    public Notification save(NotificationRequest request) {
        Notification notification = new Notification();

        notification.setAttachment(request.getAttachment());
        notification.setBody(request.getBody());
        notification.setCreateAt(LocalDate.now());
        notification.setWillSendTo(request.getWillSendTo());
        notification.setUser(userService.getByChatId(request.getChatId()));

        return notificationRepository.save(notification);
    }
}