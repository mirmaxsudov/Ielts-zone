package uz.ieltszone.ieltszoneuserservice.model.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}
