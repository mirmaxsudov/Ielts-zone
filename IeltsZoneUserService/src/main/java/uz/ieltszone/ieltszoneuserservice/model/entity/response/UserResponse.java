package uz.ieltszone.ieltszoneuserservice.model.entity.response;

import lombok.Builder;
import lombok.Setter;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.time.LocalDate;

@Setter
@Builder
public class UserResponse {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate joinedAt;
    private String email;
    private UserRole role;
    private AttachmentResponse attachment;

}