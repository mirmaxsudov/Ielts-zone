package uz.ieltszone.ieltszoneregistryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class IeltsZoneRegistryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IeltsZoneRegistryServiceApplication.class, args);
    }
}