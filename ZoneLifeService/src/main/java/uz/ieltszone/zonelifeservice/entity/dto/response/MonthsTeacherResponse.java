package uz.ieltszone.zonelifeservice.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthsTeacherResponse {
    private Month month;
    private List<TeacherResponseForMonth> teachers;
}