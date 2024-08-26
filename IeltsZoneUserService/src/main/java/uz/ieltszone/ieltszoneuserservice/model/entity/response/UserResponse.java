package uz.ieltszone.ieltszoneuserservice.model.entity.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private LocalDate joinedAt;
    private UserRole role;
    private AttachmentResponse attachmentResponse;
}