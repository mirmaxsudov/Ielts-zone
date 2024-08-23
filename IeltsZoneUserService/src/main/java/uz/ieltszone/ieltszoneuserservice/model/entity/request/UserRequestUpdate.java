package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import lombok.*;
import uz.ieltszone.ieltszoneuserservice.aop.annotations.UniqueEmailChecker;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUpdate {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    @UniqueEmailChecker
    private String email;
}