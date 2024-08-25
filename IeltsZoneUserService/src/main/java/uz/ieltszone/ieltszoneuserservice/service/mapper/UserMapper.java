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
                .joinedAt(LocalDate.now())
                .role(userRequest.getRole())
                .email(userRequest.getEmail())
                .gender(userRequest.getGender())
                .password(userRequest.getPassword())
                .lastName(userRequest.getLastName())
                .birthDate(userRequest.getBirthDate())
                .firstName(userRequest.getFirstName())
                .phoneNumber(userRequest.getPhoneNumber())
                .attachmentId(userRequest.getAttachmentId())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .birthDate(user.getBirthDate())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .joinedAt(user.getJoinedAt())
                .email(user.getEmail())
                .role(user.getRole())
                .id(user.getId())
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
