package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUpdate {
    @NotNull(message = "First name cannot be null")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    private String lastName;
    @NotNull(message = "Password cannot be null")
    private LocalDate birthDate;
    @UniqueEmailChecker
    @NotNull(message = "Email cannot be null")
    private String email;
}