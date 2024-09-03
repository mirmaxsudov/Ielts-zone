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
    public ApiResponse<MonthsTeacherResponse> getMonthOfTeachersForIELTS() {
        List<TeacherResponse> teachers = userFeign.getAllTeachers();

        if (teachers == null || teachers.isEmpty())
            throw new CustomNotFoundException("Teachers not found!");

        Month currentMonth = LocalDate.now().getMonth();


        return null;
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