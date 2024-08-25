package uz.ieltszone.zonelifeservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;

@FeignClient(name = "IELTS-ZONE-USER-SERVICE")
public interface UserFeign {
    @GetMapping("/api/v1/user/exists/{userId}")
    boolean existsById(@PathVariable Long userId);

    @GetMapping("/api/v1/user/{userId}")
    TeacherResponse getById(@PathVariable("userId") Long userId);
}