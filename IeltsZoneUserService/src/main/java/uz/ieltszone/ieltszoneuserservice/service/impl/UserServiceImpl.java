package uz.ieltszone.ieltszoneuserservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.ieltszoneuserservice.exceptions.CustomAlreadyExistException;
import uz.ieltszone.ieltszoneuserservice.exceptions.CustomBadRequestException;
import uz.ieltszone.ieltszoneuserservice.exceptions.CustomNotFoundException;
import uz.ieltszone.ieltszoneuserservice.feign.AttachmentFeign;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequestUpdate;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;
import uz.ieltszone.ieltszoneuserservice.repository.UserRepository;
import uz.ieltszone.ieltszoneuserservice.service.base.UserService;
import uz.ieltszone.ieltszoneuserservice.service.mapper.UserMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AttachmentFeign attachmentFeign;

    @Override
    public ApiResponse<UserResponse> save(UserRequest userRequest) {
        if (existsByEmail(userRequest.getEmail()))
            throw new CustomAlreadyExistException("User already exists with email: " + userRequest.getEmail());
        else if (existsByPhoneNumber(userRequest.getPhoneNumber()))
            throw new CustomAlreadyExistException("User already exists with phone number: " + userRequest.getPhoneNumber());

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

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    private User getByIdForBackend(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomNotFoundException("User not found")
                );
    }

    @Override
    public ApiResponse<UserResponse> update(UserRequestUpdate userRequest, Long userId) {
        User user = getByIdForBackend(userId);

        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setBirthDate(userRequest.getBirthDate());

        userRepository.save(user);
        return new ApiResponse<UserResponse>().success("Successfully updated", userMapper.toResponse(user));
    }

    @Override
    public Iterable<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public ApiResponse<UserResponse> updateAttachment(Long userId, Long attachmentId) {
        if (attachmentId == null)
            throw new CustomBadRequestException("Attachment id is required");
        else if (attachmentId <= 0)
            throw new CustomBadRequestException("Attachment id must be greater than 0");

        CompletableFuture.runAsync(
                () -> attachmentFeign.deleteAttachment(attachmentId)
        );

        User user = getByIdForBackend(userId);
        user.setAttachmentId(attachmentId);

        userRepository.save(user);
        return new ApiResponse<UserResponse>()
                .success("Successfully updated",
                        userMapper.toResponse(user));
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Iterable<UserResponse> getAllTeachers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().equals(UserRole.TEACHER))
                .map(userMapper::toResponse)
                .toList();
    }
}