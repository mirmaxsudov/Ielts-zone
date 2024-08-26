package uz.ieltszone.ieltszonefileservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.ieltszone.ieltszonefileservice.entity.request.RoleCheckRequest;

@FeignClient("IELTS-ZONE-USER-SERVICE")
public interface UserFeign {
    @GetMapping("/api/v1/user/auth/check-roles")
    Boolean checkRoles(@RequestBody RoleCheckRequest request);
}