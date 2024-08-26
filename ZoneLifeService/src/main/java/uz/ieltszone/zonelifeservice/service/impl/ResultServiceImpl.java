package uz.ieltszone.zonelifeservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import uz.ieltszone.zonelifeservice.entity.Exam;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.dto.request.ResultRequest;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;
import uz.ieltszone.zonelifeservice.payload.ApiResponse;
import uz.ieltszone.zonelifeservice.repository.ResultRepository;
import uz.ieltszone.zonelifeservice.service.base.ResultService;
import uz.ieltszone.zonelifeservice.service.mapper.ResultMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {
    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;

    @Override
    public Result save(ResultResponse resultResponse) {
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public void save(List<ResultRequest> resultResponses, Exam exam) {
        for (ResultRequest resultResponse : resultResponses) {
            Result result = new Result();
            result.setExam(exam);
            result.setListeningBall(resultResponse.getListening());
            result.setReadingBall(resultResponse.getReading());
            result.setSpeakingBall(resultResponse.getSpeaking());
            result.setWritingBall(resultResponse.getWriting());

//            check passed ?

            result.setIsPassed(
                    (result.getSpeakingBall() + result.getReadingBall() + result.getWritingBall() + result.getListeningBall())
                            / 4 >= exam.getPassMark()
            );

            resultRepository.save(result);
        }
    }

    @Override
    @Modifying
    @Transactional
    public void deleteByExamId(Long examId) {
        resultRepository.deleteByExamId(examId);
    }

    @Override
    public List<Result> getResultsByExamId(Long examId) {
        return resultRepository.findByExamId(examId);
    }

    @Override
    public ApiResponse<List<ResultResponse>> getAllByExamId(Long examId) {
        return new ApiResponse<List<ResultResponse>>()
                .success(
                        "Successfully fetched",
                        getResultsByExamId(examId)
                                .stream()
                                .map(resultMapper::toResponse)
                                .toList()
                );
    }
}