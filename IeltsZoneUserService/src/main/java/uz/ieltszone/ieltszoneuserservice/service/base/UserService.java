package uz.ieltszone.ieltszoneuserservice.service.base;

import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;

public interface UserService {
    ApiResponse<UserResponse> save(UserRequest userRequest);

    UserResponse getById(Long userId);

    UserResponse getByEmail(String email);

    UserResponse getByPhoneNumber(String phoneNumber);
}