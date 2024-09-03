package uz.ieltszone.zonelifeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.dto.request.RateRequest;
import uz.ieltszone.zonelifeservice.entity.enums.RateStatus;
import uz.ieltszone.zonelifeservice.repository.RateRepository;
import uz.ieltszone.zonelifeservice.service.base.RateService;

import java.time.Month;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateRepository rateRepository;

    @Override
    public Rate getByTeacherIdAndMonthAndRateLevel(Long teacherId, Month month, RateStatus status) {
        return rateRepository.findByTeacherIdAndMonthRateLevel(teacherId, month, status);
    }

    @Override
    @Modifying
    public void save(RateRequest rateRequest) {
        Rate rate = new Rate();
        rate.setTeacherId(rateRequest.getTeacherId());
        rate.setMonth(rateRequest.getMonth());
        rate.setAvg(rateRequest.getAvg());
        rate.setDate(rateRequest.getDate());
        rateRepository.save(rate);
    }

    @Override
    @Modifying
    public void save(Rate rate) {
        rateRepository.save(rate);
    }
}