package uz.ieltszone.ieltszoneuserservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.ieltszoneuserservice.exceptions.CustomNotFoundException;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;
import uz.ieltszone.ieltszoneuserservice.repository.UserRepository;
import uz.ieltszone.ieltszoneuserservice.service.base.UserService;
import uz.ieltszone.ieltszoneuserservice.service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<UserResponse> save(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        userRepository.save(user);

        return new ApiResponse<UserResponse>()
                .success("Successfully saved", userMapper.toResponse(user));
    }

    @Override
    public UserResponse getById(Long userId) {
        return userMapper.toResponse(
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new CustomNotFoundException("User not found")
                        )
        );
    }

    @Override
    public UserResponse getByEmail(String email) {
        return userMapper.toResponse(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new CustomNotFoundException("User not found")
                        )
        );
    }

    @Override
    public UserResponse getByPhoneNumber(String phoneNumber) {
        return userMapper.toResponse(
                userRepository.findByPhoneNumber(phoneNumber)
                        .orElseThrow(
                                () -> new CustomNotFoundException("User not found")
                        )
        );
    }
}