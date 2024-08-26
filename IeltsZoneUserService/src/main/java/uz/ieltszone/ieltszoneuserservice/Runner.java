package uz.ieltszone.ieltszoneuserservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.service.impl.UserServiceImpl;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final UserServiceImpl userService;

    @Override
    public void run(String... args) {
    }
}
