package uz.ieltszone.ieltszonefileservice.entity.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private Long excelFileId;
    private List<ResultResponse> resultResponses;
}