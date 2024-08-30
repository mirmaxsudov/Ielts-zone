package uz.ieltszone.writequestionsbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.writequestionsbot.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}