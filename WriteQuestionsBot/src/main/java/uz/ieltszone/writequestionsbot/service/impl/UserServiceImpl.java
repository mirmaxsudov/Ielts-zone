package uz.ieltszone.writequestionsbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.repository.UserRepository;
import uz.ieltszone.writequestionsbot.service.base.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElse(null);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
}