package uz.ieltszone.zonelifeservice.service.mapper;

import org.springframework.stereotype.Component;
import uz.ieltszone.zonelifeservice.entity.Result;
import uz.ieltszone.zonelifeservice.entity.dto.response.ResultResponse;

@Component
public class ResultMapper {
    public ResultResponse toResponse(Result result) {
        return ResultResponse.builder()
                .id(result.getId())
                .reading(result.getReadingBall())
                .writing(result.getWritingBall())
                .speaking(result.getSpeakingBall())
                .listening(result.getListeningBall())
                .total(result.getReadingBall() + result.getWritingBall() + result.getSpeakingBall() + result.getListeningBall())
                .build();
    }
}