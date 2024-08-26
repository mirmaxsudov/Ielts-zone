package uz.ieltszone.ieltszoneuserservice.model.entity.response;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String phoneNumber;
    private String email;
}