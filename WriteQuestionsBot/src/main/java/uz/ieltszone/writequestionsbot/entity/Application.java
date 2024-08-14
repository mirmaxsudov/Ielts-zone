package uz.ieltszone.writequestionsbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionAsText;
    private String answerAsText;
    private LocalDateTime createdAt;
    private String whenTime;
    @OneToMany(mappedBy = "application")
    private List<Attachment> attachments;
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}