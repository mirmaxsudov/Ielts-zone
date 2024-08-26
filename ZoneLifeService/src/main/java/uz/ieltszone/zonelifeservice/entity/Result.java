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
    private Float writingBall;
    private Float readingBall;
    private Float speakingBall;
    private Float listeningBall;
    private Boolean isPassed;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;
}