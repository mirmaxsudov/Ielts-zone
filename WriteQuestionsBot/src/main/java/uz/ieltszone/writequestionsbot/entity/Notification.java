package uz.ieltszone.writequestionsbot.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String body;
    @ManyToOne
    private Attachment attachment;
    @ManyToOne
    private User user;
    private LocalDate createAt;
    @Enumerated(EnumType.STRING)
    private UserRole willSendTo;
}