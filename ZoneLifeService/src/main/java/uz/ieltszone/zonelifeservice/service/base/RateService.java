package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.dto.request.RateRequest;

import java.time.Month;

public interface RateService {
    Rate getByTeacherIdAndMonth(Long teacherId, Month month);

    void save(RateRequest rateRequest);
}
