package uz.ieltszone.zonelifeservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.zonelifeservice.config.security.UserDetailsDTO;
import uz.ieltszone.zonelifeservice.entity.dto.request.RoleCheckRequest;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;

@FeignClient(name = "IELTS-ZONE-USER-SERVICE")
public interface UserFeign {
    @GetMapping("/api/v1/user/exists/{userId}")
    boolean existsById(@PathVariable Long userId);

    @GetMapping("/api/v1/user/{userId}")
    TeacherResponse getById(@PathVariable("userId") Long userId);

    @PostMapping("/api/v1/user/auth/check-roles")
    Boolean checkRoles(@RequestBody RoleCheckRequest roleCheckRequest);

    @GetMapping("/api/v1/user/auth/me")
    UserDetailsDTO getUserDetails(@RequestHeader("Authorization") String token);
}