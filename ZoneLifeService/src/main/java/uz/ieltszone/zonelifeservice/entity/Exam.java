package uz.ieltszone.zonelifeservice.entity;

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
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double writingTotalBall;
    private Double readingTotalBall;
    private Double speakingTotalBall;
    private Double listeningTotalBall;
    private Long excelFileId;
    private LocalDateTime addedAt;
    private Long teacherId;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results;
}