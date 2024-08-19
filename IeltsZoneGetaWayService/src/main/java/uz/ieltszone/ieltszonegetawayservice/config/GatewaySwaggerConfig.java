package uz.ieltszone.ieltszonegetawayservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewaySwaggerConfig {
    @Bean
    public OpenAPI getawayOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Ielts Zone Getaway Service API")
                                .version("1.0")
                );
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("Gateway")
                .pathsToMatch("/api/v1/attachment/**", "/api/v1/user/**", "/api/v1/ielts-zone/**")
                .build();
    }
}