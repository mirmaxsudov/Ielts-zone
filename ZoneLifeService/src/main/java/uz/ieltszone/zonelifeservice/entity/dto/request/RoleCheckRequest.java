package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleCheckRequest {
    private String token;
    private List<String> roles;
}
