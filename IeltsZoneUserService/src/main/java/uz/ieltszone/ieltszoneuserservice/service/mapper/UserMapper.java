package uz.ieltszone.ieltszoneuserservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ieltszone.ieltszoneuserservice.feign.AttachmentFeign;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.AttachmentResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final AttachmentFeign attachmentFeign;

    public User toEntity(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .joinedAt(LocalDate.now())
                .role(userRequest.getRole())
                .birthDate(userRequest.getBirthDate())
                .attachmentId(userRequest.getAttachmentId())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .joinedAt(user.getJoinedAt())
                .email(user.getEmail())
                .role(user.getRole())
                .attachmentResponse(
                        user.getAttachmentId() == null ? null :
                                AttachmentResponse.builder()
                                        .attachmentId(user.getAttachmentId())
                                        .url("http://localhost:8082/api/v1/attachment/" + user.getAttachmentId())
                                        .build()
                )
                .build();
    }
}
