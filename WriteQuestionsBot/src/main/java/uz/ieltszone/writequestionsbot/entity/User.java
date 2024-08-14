package uz.ieltszone.writequestionsbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuppressWarnings("JpaDataSourceORMInspection")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\+998?[0-9]{9}$", message = "Phone number is not valid")
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private Long chatId;
    private Boolean isBlock;
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.STUDENT;

    @OneToMany(mappedBy = "user")
    private List<Application> applications;
}