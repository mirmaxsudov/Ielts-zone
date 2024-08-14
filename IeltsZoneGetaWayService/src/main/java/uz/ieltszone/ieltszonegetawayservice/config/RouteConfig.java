package uz.ieltszone.ieltszonegetawayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder locatorBuilder) {
        return locatorBuilder.routes()
                .route(r -> r.path("/api/v1/zone-life/**")
                        .uri("lb://ZONE-LIFE-SERVICE")
                ).route(
                        r -> r.path("/api/v1/file/**")
                                .uri("lb://IELTS-ZONE-FILE-SERVICE")
                )
                .route(
                        r -> r.path("/api/v1/user/**")
                                .uri("lb://IELTS-ZONE-USER-SERVICE")
                )
                .build();
    }
}