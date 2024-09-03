package uz.ieltszone.zonelifeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ieltszone.zonelifeservice.entity.enums.RateStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long teacherId;
    private LocalDate date;
    private Month month;
    private Float avg;
    private RateStatus status;
    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exam> exams;
}