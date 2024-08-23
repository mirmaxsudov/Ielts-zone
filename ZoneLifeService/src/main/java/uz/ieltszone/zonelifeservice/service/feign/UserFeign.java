package uz.ieltszone.zonelifeservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "IELTS-ZONE-USER-SERVICE")
public interface UserFeign {
    @GetMapping("/api/v1/user/exists/{userId}")
    boolean existsById(@PathVariable Long userId);
}