package uz.ieltszone.zonelifeservice.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.ieltszone.zonelifeservice.exceptions.feign.CustomFeignErrorDecoder;

@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}