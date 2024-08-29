package uz.ieltszone.zonelifeservice.entity.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateTeacherBack {
    private TeacherResponse teacherResponse;
    private Rate rate;
}
