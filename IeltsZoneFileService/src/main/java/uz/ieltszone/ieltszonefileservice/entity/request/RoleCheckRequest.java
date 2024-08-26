package uz.ieltszone.ieltszonefileservice.entity.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCheckRequest {
    private String token;
    private List<String> roles;
}
