package uz.ieltszone.ieltszoneuserservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuppressWarnings("all")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDate joinedAt;
    private String email;
    private UserRole role;
    private Long attachmentId;
}