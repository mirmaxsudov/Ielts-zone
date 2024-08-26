package uz.ieltszone.ieltszonefileservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uz.ieltszone.ieltszonefileservice.config.security.UserDetailsDTO;
import uz.ieltszone.ieltszonefileservice.entity.request.RoleCheckRequest;

@FeignClient("IELTS-ZONE-USER-SERVICE")
public interface UserFeign {
    @PostMapping("/api/v1/user/auth/check-roles")
    Boolean checkRoles(@RequestBody RoleCheckRequest request);

    @GetMapping("/api/v1/user/auth/me")
    UserDetailsDTO getUserDetails(@RequestHeader("Authorization") String token);
}