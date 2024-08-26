package uz.ieltszone.ieltszoneuserservice.model.entity.request;

import lombok.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCheckRequest {
    private String token;
    private List<UserRole> roles;
}
