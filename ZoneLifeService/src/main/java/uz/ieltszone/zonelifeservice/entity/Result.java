package uz.ieltszone.zonelifeservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "result")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double writingBall;
    private Double readingBall;
    private Double speakingBall;
    private Double listeningBall;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;
}