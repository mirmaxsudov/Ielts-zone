package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;
    private String password;
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone number is not valid")
    private String phoneNumber;
    private LocalDate birthDate;
    @UniqueEmailChecker
    private String email;
    private UserRole role;
    private Long attachmentId;
}