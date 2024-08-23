package uz.ieltszone.ieltszoneuserservice.service.base;

import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequestUpdate;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;

public interface UserService {
    ApiResponse<UserResponse> save(UserRequest userRequest);

    UserResponse getById(Long userId);

    UserResponse getByEmail(String email);

    UserResponse getByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    ApiResponse<UserResponse> update(UserRequestUpdate userRequest, Long userId);

    Iterable<UserResponse> getAll();

    ApiResponse<UserResponse> updateAttachment(Long userId, Long attachmentId);

    boolean existsById(Long userId);
}