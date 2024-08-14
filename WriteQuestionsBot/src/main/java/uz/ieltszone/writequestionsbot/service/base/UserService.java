package uz.ieltszone.writequestionsbot.service.base;

import uz.ieltszone.writequestionsbot.entity.User;

public interface UserService {
    User getByChatId(Long chatId);
    boolean existsByPhoneNumber(String phoneNumber);

    void save(User user);
}