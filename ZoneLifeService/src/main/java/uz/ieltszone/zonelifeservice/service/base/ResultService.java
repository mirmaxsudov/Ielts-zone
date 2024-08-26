package uz.ieltszone.zonelifeservice.service.base;

import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.dto.request.ResultRequest;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;

import java.util.List;

public interface ResultService {
    Result save(ResultResponse resultResponse);

    void save(List<ResultRequest> resultRequests, Exam exam);

    void deleteByExamId(Long examId);

    List<Result> getResultsByExamId(Long examId);
}
