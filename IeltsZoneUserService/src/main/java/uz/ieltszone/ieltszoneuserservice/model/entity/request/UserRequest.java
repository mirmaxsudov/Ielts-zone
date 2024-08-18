package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import lombok.*;
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
    private String username;
    private String password;
    private String phoneNumber;
    private LocalDate birthDate;
    private String email;
    private UserRole role;
}