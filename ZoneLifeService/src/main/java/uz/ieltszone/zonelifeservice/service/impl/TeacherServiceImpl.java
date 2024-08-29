package uz.ieltszone.zonelifeservice.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.dto.response.MonthsTeacherResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponse;
import uz.ieltszone.zonelifeservice.entity.dto.response.TeacherResponseForMonth;
import uz.ieltszone.zonelifeservice.entity.payload.RateTeacherBack;
import uz.ieltszone.zonelifeservice.exceptions.CustomNotFoundException;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.service.base.RateService;
import uz.ieltszone.zonelifeservice.service.base.RewardService;
import uz.ieltszone.zonelifeservice.service.base.TeacherService;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final UserFeign userFeign;
    private final RewardService rewardService;
    private final RateService rateService;

    @Override
    public ApiResponse<TeacherResponse> getById(Long teacherId) {
        TeacherResponse teacher = userFeign.getById(teacherId);

        if (teacher == null)
            throw new CustomNotFoundException("Teacher not found!");

        return new ApiResponse<TeacherResponse>()
                .success(
                        "Teacher found",
                        teacher
                );
    }

    @Override
    public ApiResponse<Long> getRatingForTeacherAndAllTheTime(Long teacherId) {
        return null;
    }

    @Override
    public ApiResponse<MonthsTeacherResponse> getMonthOfTeachers() {
        List<TeacherResponse> teachers = userFeign.getAll();

        if (teachers == null || teachers.isEmpty())
            throw new CustomNotFoundException("Teachers not found!");

        Map<Long, RateTeacherBack> mpRate = new HashMap<>();

        Month month = LocalDate.now().getMonth();
        for (TeacherResponse teacher : teachers) {
            Rate rate = rateService.getByTeacherIdAndMonth(teacher.getId(), month);

            RateTeacherBack rateTeacherBack = new RateTeacherBack();
            rateTeacherBack.setRate(rate);
            rateTeacherBack.setTeacherResponse(teacher);
        }

        Map<Long, RateTeacherBack> sortedMap = sortByAvgDescending(mpRate);

        MonthsTeacherResponse monthsTeacherResponse = new MonthsTeacherResponse();
        monthsTeacherResponse.setMonth(month);

        List<TeacherResponseForMonth> topThreeTeachers = new ArrayList<>();

        for (Map.Entry<Long, RateTeacherBack> entry : sortedMap.entrySet()) {
            if (topThreeTeachers.size() == 3) break;

            RateTeacherBack rateTeacherBack = entry.getValue();

            TeacherResponseForMonth teacher = new TeacherResponseForMonth();
            teacher.setAvg(rateTeacherBack.getRate().getAvg());
            teacher.setResponse(rateTeacherBack.getTeacherResponse());

            topThreeTeachers.add(teacher);
        }

        monthsTeacherResponse.setTeachers(topThreeTeachers);

        return new ApiResponse<MonthsTeacherResponse>()
                .success("Teachers found",
                        monthsTeacherResponse);
    }

    public static Map<Long, RateTeacherBack> sortByAvgDescending(Map<Long, RateTeacherBack> unsortedMap) {
        List<Map.Entry<Long, RateTeacherBack>> entryList = new ArrayList<>(unsortedMap.entrySet());

        entryList.sort((e1, e2) -> Float.compare(e2.getValue().getRate().getAvg(),
                e1.getValue().getRate().getAvg()));

        Map<Long, RateTeacherBack> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Long, RateTeacherBack> entry : entryList)
            sortedMap.put(entry.getKey(), entry.getValue());

        return sortedMap;
    }
}