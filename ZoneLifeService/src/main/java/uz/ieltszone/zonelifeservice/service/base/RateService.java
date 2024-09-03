package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.dto.request.RateRequest;
import uz.ieltszone.zonelifeservice.entity.enums.RateStatus;

import java.time.Month;

public interface RateService {
    Rate getByTeacherIdAndMonthAndRateLevel(Long teacherId, Month month, RateStatus status);

    void save(RateRequest rateRequest);

    void save(Rate rate);
}
