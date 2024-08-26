package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "Login cannot be null")
    private String login;
    @NotNull(message = "Password cannot be null")
    private String password;
}