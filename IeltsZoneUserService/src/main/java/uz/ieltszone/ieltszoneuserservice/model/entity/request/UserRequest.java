package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.Gender;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.time.LocalDate;

@Valid
@Getter
@Setter
@Builder
@ToString
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotNull(message = "First name cannot be null")
    private String firstName;
    private String lastName;
    @NotNull(message = "Password cannot be null")
    private String password;
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone number is not valid")
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Birth date cannot be null")
    private LocalDate birthDate;
    @Email
    @UniqueEmailChecker
    @NotNull(message = "Email cannot be null")
    private String email;
    @NotNull(message = "Role cannot be null")
    private UserRole role;
    private Gender gender;
    private Long attachmentId;
}