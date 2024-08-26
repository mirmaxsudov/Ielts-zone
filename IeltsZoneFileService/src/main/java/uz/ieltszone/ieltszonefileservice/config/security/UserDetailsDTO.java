package uz.ieltszone.ieltszonefileservice.config.security;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String phoneNumber;
    private String email;
}