package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.response.ResultResponse;

import java.util.List;

public interface ResultService {
    Result save(ResultResponse resultResponse);

    void save(List<ResultResponse> resultResponses, Exam exam);

    void deleteByExamId(Long examId);
}
