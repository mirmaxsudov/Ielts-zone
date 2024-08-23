package uz.ieltszone.zonelifeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ieltszone.zonelifeservice.entity.enums.ExamLevel;
import uz.ieltszone.zonelifeservice.entity.enums.ExamType;

import java.time.LocalDate;
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
    private Float writing;
    private Float reading;
    private Float speaking;
    private Float listening;
    private Float total;
    private LocalDate examDate;
    private LocalDate addedAt;
    private Long teacherId;
    private Long excelFileId;

    @Enumerated(EnumType.STRING)
    private ExamLevel examLevel;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id")
    private Rate rate;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results;
}