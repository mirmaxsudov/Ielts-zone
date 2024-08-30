package uz.ieltszone.writequestionsbot.service.base;

import uz.ieltszone.writequestionsbot.entity.Notification;
import uz.ieltszone.writequestionsbot.entity.request.NotificationRequest;

public interface NotificationService {
    Notification save(NotificationRequest request);
}