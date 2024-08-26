package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateRequest {
    private Long teacherId;
    private Month month;
    private Float avg;
    private LocalDate date;
}