package uz.ieltszone.zonelifeservice.entity.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequestInner {
    private Long excelFileId;
    private List<ResultRequest> resultResponses;
}