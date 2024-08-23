package uz.ieltszone.zonelifeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.request.ResultRequest;
import uz.ieltszone.zonelifeservice.entity.response.ResultResponse;
import uz.ieltszone.zonelifeservice.repository.ResultRepository;
import uz.ieltszone.zonelifeservice.service.base.ResultService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {
    private final ResultRepository resultRepository;

    @Override
    public Result save(ResultResponse resultResponse) {
        return null;
    }

    @Override
    public void save(List<ResultRequest> resultResponses, Exam exam) {
        for (ResultRequest resultResponse : resultResponses) {
            Result result = new Result();
            result.setExam(exam);
            result.setListeningBall(resultResponse.getListening());
            result.setReadingBall(resultResponse.getReading());
            result.setSpeakingBall(resultResponse.getSpeaking());
            result.setWritingBall(resultResponse.getWriting());

            resultRepository.save(result);
        }
    }

    @Override
    public void deleteByExamId(Long examId) {
        resultRepository.deleteByExamId(examId);
    }
}